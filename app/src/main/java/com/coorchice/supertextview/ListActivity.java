/*
 * Copyright (C) 2018 CoorChice <icechen_@outlook.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * <p>
 * Last modified 18-5-13 下午6:28
 */

package com.coorchice.supertextview;

import java.util.ArrayList;
import java.util.List;

import com.coorchice.library.SuperTextView;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ListActivity extends Activity {

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rv = (RecyclerView) findViewById(R.id.rv);

        final List<String> datas = new ArrayList<>();
        datas.add("https://raw.githubusercontent.com/chenBingX/img/master/stv/wow_avatar.jpg");
        for (int i = 0; i < 20; i++) {
            datas.add("https://raw.githubusercontent.com/chenBingX/img/master/其它/u=343452579,826911251&fm=26&gp=0.jpg");
            datas.add("https://raw.githubusercontent.com/chenBingX/img/master/其它/u=2111584605,3825924903&fm=26&gp=0.jpg");
            datas.add("https://raw.githubusercontent.com/chenBingX/img/master/其它/u=2294437324,1126471106&fm=26&gp=0.jpg");
            datas.add("https://raw.githubusercontent.com/chenBingX/img/master/其它/download.jpg");
            datas.add("https://raw.githubusercontent.com/chenBingX/img/master/其它/u=3680923293,1495896930&fm=26&gp=0.jpg");
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(
                        LayoutInflater.from(ListActivity.this).inflate(
                                viewType == 0 ? R.layout.item_layout_2 : R.layout.item_layout
                                , parent, false)) {

                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (position == 0) {
                    ((SuperTextView) (holder.itemView.findViewById(R.id.stv))).setUrlImage(datas.get(position));
                } else {
                    SuperTextView stv = (SuperTextView) (holder.itemView.findViewById(R.id.stv_1));
                    stv.setDrawable(ListActivity.this.getResources().getDrawable(R.drawable.loading_1));
                    stv.setUrlImage(datas.get(position), false);
                }
            }

            @Override
            public int getItemCount() {
                return datas.size();
            }

            @Override
            public int getItemViewType(int position) {
                return position == 0 ? 0 : 1;
            }
        });
    }
}
