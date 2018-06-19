# RecyclerWheelPicker
基于recyclerview的滚轮选择器,支持自定义view,自定义滚动特效,自定义滚轮样式,万条数据也不会卡顿,流畅滚动


## 使用例子
```
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                            RecyclerWheelPicker<Integer> recyclerView = new RecyclerWheelPicker<>(getContext());
                            recyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 200)));
//                            recyclerView.setSelectedTopAreaDrawer(new RecyclerWheelPicker.AreaDrawer() {//设置顶部样式
//                                Paint paint=new Paint();
//                                {
//                                    paint.setColor(Color.argb(180,0,255,200));
//                                    paint.setAntiAlias(true);
//                                    paint.setStrokeWidth(20);
//                                }
//                                @Override
//                                public void onDraw(Context context, Canvas canvas, Rect rect) {
//                                    canvas.drawRect(rect,paint);
//                                }
//                            });
//                            recyclerView.setSelectedBottomAreaDrawer(new RecyclerWheelPicker.AreaDrawer() {//设置底部样式
//                                Paint paint=new Paint();
//                                {
//                                    paint.setColor(Color.argb(180,255,0,200));
//                                    paint.setAntiAlias(true);
//                                    paint.setStrokeWidth(20);
//                                }
//                                @Override
//                                public void onDraw(Context context, Canvas canvas, Rect rect) {
//                                    canvas.drawRect(rect,paint);
//                                }
//                            });
                            recyclerView.setMaxShowSize(5);
                            recyclerView.setSelectedAreaHeight(100);
                            recyclerView.setAdapter(new RecyclerWheelPicker.TextViewWheelAdapter<Integer>() {
                                @Override
                                String getWheelItemName(int position, Integer integer) {
                                    return "name:"+(integer+1);
                                }

                                @Override
                                void onWheelSelected(RecyclerView.ViewHolder holder, int position, Integer integer) {
                                    Log.e("onSelected2","value:"+integer);
                                }

                                @Override
                                Integer getWheelItemData(int position) {
                                    return position;
                                }

                                @Override
                                int getWheelItemCount() {
                                    return 999999;
                                }
                            });
                            bottomSheetDialog.setContentView(recyclerView);
                            bottomSheetDialog.show();

```
