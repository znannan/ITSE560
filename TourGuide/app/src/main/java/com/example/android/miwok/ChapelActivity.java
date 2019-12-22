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

public class ChapelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_list);

        // Create a list of worlds
        ArrayList<World> worlds = new ArrayList<World>();


        worlds.add(new World("Chrismas day", "members performers", R.drawable.chapel1));
        worlds.add(new World("Chrismas day", "kids performers", R.drawable.chapel2));
        worlds.add(new World("Chrismas day", "海报", R.drawable.chapel3));
        worlds.add(new World("OC", "OC in praying", R.drawable.chapel4));
        worlds.add(new World("P.Thomas", "P.Thomas 开拓", R.drawable.chapel5));
        worlds.add(new World("Chapel", "city in the sky", R.drawable.chapel6));
        worlds.add(new World("OC", "OC in praying", R.drawable.chapel7));

        // Create an {@link WorldAdapter}, whose data source is a list of {@link World}s. The
        // adapter knows how to create list items for each item in the list.
        WorldAdapter adapter = new WorldAdapter(this, worlds, R.color.category_chapel);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // world_listt.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WorldAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link World} in the list.
        listView.setAdapter(adapter);
    }
}
