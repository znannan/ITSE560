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

public class AcademyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_list);

        // Create a list of worlds
        ArrayList<World> worlds = new ArrayList<World>();
        worlds.add(new World("acadmy1", "acadmy1", R.drawable.acadmy1));
        worlds.add(new World("acadmy2", "acadmy2", R.drawable.acadmy2));
        worlds.add(new World("acadmy3", "acadmy3", R.drawable.acadmy3));
        worlds.add(new World("acadmy4", "acadmy4", R.drawable.acadmy4));
        worlds.add(new World("acadmy5", "acadmy5", R.drawable.acadmy5));
        worlds.add(new World("acadmy6", "acadmy6", R.drawable.acadmy6));
        worlds.add(new World("acadmy7", "acadmy7", R.drawable.acadmy7));

        // Create an {@link WorldAdapter}, whose data source is a list of {@link World}s. The
        // adapter knows how to create list items for each item in the list.
        WorldAdapter adapter = new WorldAdapter(this, worlds, R.color.category_academy);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // world_listt.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WorldAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link World} in the list.
        listView.setAdapter(adapter);
    }
}
