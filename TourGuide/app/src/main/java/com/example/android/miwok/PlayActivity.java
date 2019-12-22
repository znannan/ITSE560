/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.miwok;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_list);

        // Create a list of worlds
        ArrayList<World> worlds = new ArrayList<World>();
        worlds.add(new World("play1 day", "play1", R.drawable.play1));
        worlds.add(new World("play2", "play2", R.drawable.play2));
        worlds.add(new World("play3", "play3", R.drawable.play3));
        worlds.add(new World("play4", "play4", R.drawable.play4));
        worlds.add(new World("play5", "play5", R.drawable.play5));
        worlds.add(new World("play6", "play6", R.drawable.play6));
        worlds.add(new World("play7", "play7", R.drawable.play7));

        // Create an {@link WorldAdapter}, whose data source is a list of {@link World}s. The
        // adapter knows how to create list items for each item in the list.
        WorldAdapter adapter = new WorldAdapter(this, worlds, R.color.category_play);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // world_list.xmll layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WorldAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link World} in the list.
        listView.setAdapter(adapter);
    }
}
