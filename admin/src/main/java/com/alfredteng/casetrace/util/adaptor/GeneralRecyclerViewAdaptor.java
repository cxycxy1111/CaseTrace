package com.alfredteng.casetrace.util.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfredteng.casetrace.R;
import com.example.alfredtools.NetUtil;


import java.util.ArrayList;
import java.util.Map;

public class GeneralRecyclerViewAdaptor extends RecyclerView.Adapter implements View.OnClickListener{

    public static final int TYPE_HEAD = 1;
    public static final int TYPE_BODY = 2;
    public static final int TYPE_ADMIN = 3;
    public static final int TYPE_EMPTY = 0;
    public static final int TYPE_LOADING = -1;
    public static final int TYPE_ERROR = -2;
    public static final int TYPE_NET_ERROR = -3;
    public static final int TYPE_LOAD_MORE = -4;
    public static final int TYPE_END = -5;
    private Context context;
    public String str_body_key = "";
    private ArrayList<Map<String,String>> arrayList;
    private OnItemClickListener onItemClickListener = null;
    private OnLoadMoreClickListener onLoadMoreClickListener = null;

    public GeneralRecyclerViewAdaptor(ArrayList<Map<String, String>> list, Context context, String str_body_key) {
        this.arrayList = list;
        this.context = context;
        this.str_body_key = str_body_key;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup vg;
        switch (viewType) {
            case TYPE_HEAD:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_head,parent,false);
                return new Head(vg);
            case TYPE_BODY:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_body,parent,false);
                vg.setOnClickListener(this);
                return new Body(vg);
            case TYPE_ADMIN:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_admin_list,parent,false);
                vg.setOnClickListener(this);
                return new AdminList(vg);
            case TYPE_EMPTY:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_empty,parent,false);
                return new Empty(vg);
            case TYPE_LOADING:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_loading,parent,false);
                return new Loading(vg);
            case TYPE_ERROR:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_empty,parent,false);
                return new Empty(vg);
            case TYPE_NET_ERROR:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_empty,parent,false);
                return new Empty(vg);
            case TYPE_LOAD_MORE:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_load_more,parent,false);
                vg.setOnClickListener(this);
                return new LoadMore(vg);
            case TYPE_END:
                vg = (ViewGroup)inflater.inflate(R.layout.tile_rv_end,parent,false);
                return new End(vg);
            default:return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEAD:
                final Head head = (Head) holder;
                head.itemView.setTag(position);
                head.tv_head.setText(arrayList.get(position).get("text"));
                break;
            case TYPE_BODY:
                final Body body = (Body) holder;
                body.itemView.setTag(position);
                body.tv_head.setText(arrayList.get(position).get(str_body_key));
                break;
            case TYPE_ADMIN:
                final AdminList adminList = (AdminList) holder;
                adminList.itemView.setTag(position);
                adminList.tv.setText(arrayList.get(position).get(str_body_key));
                break;
            case TYPE_EMPTY:
                final Empty empty = (Empty) holder;
                empty.itemView.setTag(position);
                empty.tv.setText("暂无数据");
                break;
            case TYPE_LOADING:
                final Loading loading = (Loading) holder;
                loading.itemView.setTag(position);
                break;
            case TYPE_ERROR:
                final Empty empty1 = (Empty)holder;
                empty1.itemView.setTag(position);
                empty1.tv.setText(NetUtil.UNKNOWN_ERROR);
                break;
            case TYPE_NET_ERROR:
                final Empty empty2 = (Empty)holder;
                empty2.itemView.setTag(position);
                empty2.tv.setText(NetUtil.CANT_CONNECT_INTERNET);
                break;
            case TYPE_LOAD_MORE:
                final LoadMore load_more = (LoadMore) holder;
                load_more.itemView.setTag(position);
                load_more.tv.setText("加载更多");
                break;
            case TYPE_END:
                final End end = (End) holder;
                end.itemView.setTag(position);
                end.tv.setText("已加载完全部数据");
                break;
            default:break;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(String.valueOf(arrayList.get(position).get("holder_type")));
    }

    private static class Empty extends RecyclerView.ViewHolder {
        TextView tv;
        private Empty(View view){
            super(view);
            tv = (TextView)itemView.findViewById(R.id.t_empty_body);
        }
    }

    private static class Head extends RecyclerView.ViewHolder {
        TextView tv_head;
        private Head(View view){
            super(view);
            tv_head = (TextView)itemView.findViewById(R.id.t_rv_head_text);
        }
    }

    private static class Body extends RecyclerView.ViewHolder {
        TextView tv_head;
        private Body(View view){
            super(view);
            tv_head = (TextView)itemView.findViewById(R.id.t_rv_body_text);
        }
    }

    private static class AdminList extends RecyclerView.ViewHolder {
        TextView tv;
        private AdminList(View view){
            super(view);
            tv = (TextView)itemView.findViewById(R.id.t_rv_admin_list_text);
        }
    }

    private static class LoadMore extends RecyclerView.ViewHolder {
        TextView tv;
        private LoadMore(View view){
            super(view);
            tv = (TextView)itemView.findViewById(R.id.t_rv_load_more_text);
        }
    }

    private static class End extends RecyclerView.ViewHolder {
        TextView tv;
        private End(View view){
            super(view);
            tv = (TextView)itemView.findViewById(R.id.t_rv_end_text);
        }
    }

    private static class Loading extends RecyclerView.ViewHolder {
        private Loading(View view){
            super(view);
        }
    }

    public String getStr_body_key() {
        return str_body_key;
    }

    public void setStr_body_key(String str_body_key) {
        this.str_body_key = str_body_key;
    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public interface OnLoadMoreClickListener {
        void onLoadMoreClick(View view,int position);
    }

    public void setOnLoadMoreClickListener(OnLoadMoreClickListener onLoadMoreClickListener) {
        this.onLoadMoreClickListener = onLoadMoreClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_t_load_more:
                onLoadMoreClickListener.onLoadMoreClick(v,(int)v.getTag());
                break;
            default:
                onItemClickListener.onItemClick(v,(int)v.getTag());
                break;
        }
    }
}
