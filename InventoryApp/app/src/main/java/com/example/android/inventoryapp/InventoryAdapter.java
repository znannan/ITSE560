package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.MyViewHolder> {

    /**
     * Inventory data in the {@link Cursor}.
     */
    private Cursor mCursor;

    /**
     * {@link Context} that passed in through the constructor.
     */
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public InventoryAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public InventoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(itemView);
        // Setup each item listener here.
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = myViewHolder.getAdapterPosition();
                mCursor.moveToPosition(position);
                if (mOnItemClickListener != null) {
                    // Send the click event back to the host activity.
                    mOnItemClickListener.onItemClick(view, position, getItemId(position));
                }
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryAdapter.MyViewHolder holder, int position) {
        //  Need to move cursor manually.
        if (mCursor.moveToPosition(position)) {
            // Find the necessary columns of inventory attributes.
            int imageColumnIndex = mCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_IMAGE);
            int nameColumnIndex = mCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = mCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int numberRemainingColumnIndex =
                    mCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING);
            int numberSoldColumnIndex =
                    mCursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NUMBER_SOLD);

            // Extract out the value from the Cursor.
            String imageUriString = mCursor.getString(imageColumnIndex);
            String nameString = mCursor.getString(nameColumnIndex);
            String priceString = mCursor.getString(priceColumnIndex);
            final long numberRemaining = mCursor.getLong(numberRemainingColumnIndex);
            final long numberSold = mCursor.getLong(numberSoldColumnIndex);

            // Set the values to the views.
            GlideApp.with(mContext).load(imageUriString)
                    .transforms(new CenterCrop(), new RoundedCorners(
                            (int) mContext.getResources().getDimension(R.dimen.background_corner_radius)))
                    .into(holder.imageView);
            holder.nameTextView.setText(nameString);
            holder.priceTextView.setText(
                    mContext.getString(R.string.item_price, Double.valueOf(priceString)));
            holder.quantityTextView.setText(
                    mContext.getString(R.string.item_quantity, numberSold, numberRemaining));

            // Setup the sell fab listener here.
            holder.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Call a helper method to update quantity.
                    updateQuantity(getItemId(holder.getAdapterPosition()),
                            numberSold, numberRemaining);
                }
            });
        }
    }

    /**
     * Helper method that update item quantity when the selling fab was clicked.
     *
     * @param id              of item in database whose quantity need to update.
     * @param numberSold      of item before adding one to it.
     * @param numberRemaining of item before minus one to it.
     */
    private void updateQuantity(long id, long numberSold, long numberRemaining) {
        if (numberRemaining > 0) {
            // If the item has remaining stocks, then update the numbers.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_ITEM_NUMBER_SOLD, numberSold + 1);
            values.put(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING, numberRemaining - 1);

            Uri currentItemUri = ContentUris.withAppendedId(
                    InventoryEntry.CONTENT_URI, id);

            int rowAffected = mContext.getContentResolver().update(
                    currentItemUri, values, null, null);

            if (rowAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(mContext, mContext.getString(R.string.toast_update_failed),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_out_of_stock),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Helper method that get ID of item in database from its position.
     *
     * @param position of the item in the {@link RecyclerView} list.
     * @return ID of the item in database.
     */
    public long getItemId(int position) {
        if (mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                // Find the ID column of inventory attributes.
                int idColumnIndex = mCursor.getColumnIndex(InventoryEntry._ID);
                // Return the ID from cursor for the current item.
                return mCursor.getLong(idColumnIndex);
            }
        }

        return 0;
    }

    /**
     * Helper method that notify {@link InventoryAdapter} that data have changed.
     *
     * @param cursor is the updated data that passed in.
     */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView, priceTextView, quantityTextView;
        public FloatingActionButton fab;

        public MyViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.item_image);
            nameTextView = view.findViewById(R.id.item_name);
            priceTextView = view.findViewById(R.id.item_price);
            quantityTextView = view.findViewById(R.id.item_quantity);
            fab = view.findViewById(R.id.fab_sell);
        }
    }
}
