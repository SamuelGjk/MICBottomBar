/*
 * Copyright 2017 SamuelGjk. https://github.com/SamuelGjk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package moe.yukinoneko.micbottombar.sample

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import moe.yukinoneko.micbottombar.MICBottomBar

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar.addNavigationItems(
                MICBottomBar.NavigationItem(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "F 1"),
                MICBottomBar.NavigationItem(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "F 2"),
                MICBottomBar.NavigationItem(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "F 3"),
                MICBottomBar.NavigationItem(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "F 4"),
                MICBottomBar.NavigationItem(R.mipmap.ic_launcher, R.mipmap.ic_launcher_round, "F 5")
        )

        bottomBar.setOnNavigationItemClickListener(object : MICBottomBar.OnNavigationItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                fragmentManager.beginTransaction()
                        .replace(R.id.contentContainer, ContentFragment.newInstance(position + 1))
                        .commit()
            }
        })

        fragmentManager.beginTransaction()
                .replace(R.id.contentContainer, ContentFragment.newInstance(1))
                .commit()
    }
}
