package com.alfredteng.casetrace.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfredteng.casetrace.R;
import com.alfredteng.casetrace.admin.AdminListActivity;
import com.alfredteng.casetrace.cases.CaseListActivity;
import com.alfredteng.casetrace.company.CompanyListActivity;
import com.alfredteng.casetrace.event.EventListActivity;
import com.alfredteng.casetrace.product.ProductListActivity;
import com.alfredteng.casetrace.timeline.TimelineListActivity;
import com.alfredteng.casetrace.user.UserListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {
    private static final String KEY_1 = "position";
    private static final String KEY_2 = "title";

    private RecyclerView recyclerView;
    private int position;
    private String title;
    private Context context;
    private ArrayList<Map<String,String>> arrayList;
    private RecyclerViewAdaptor1 adaptor;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
    }

    public static MainFragment newInstance(int position,String title) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_1, position);
        args.putString(KEY_2,title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(KEY_1);
            title = getArguments().getString(KEY_2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_case_and_event, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rv_f_main_case_and_event);
        initArrayList();
        return view;
    }

    private void initArrayList() {
        arrayList = new ArrayList<Map<String, String>>();
        switch (position) {
            case 1:
                Map<String,String> map_head_case = new HashMap<String,String>();
                Map<String,String> map_body_case_unchecked = new HashMap<String,String>();
                Map<String,String> map_body_case_rejected = new HashMap<String,String>();
                Map<String,String> map_body_case_passed = new HashMap<String,String>();
                Map<String,String> map_body_case_deleted = new HashMap<String,String>();
                Map<String,String> map_head_event = new HashMap<String,String>();
                Map<String,String> map_body_event_unchecked = new HashMap<String,String>();
                Map<String,String> map_body_event_rejected = new HashMap<String,String>();
                Map<String,String> map_body_event_passed = new HashMap<String,String>();
                Map<String,String> map_body_event_deleted = new HashMap<String,String>();
                Map<String,String> map_head_timeline = new HashMap<String,String>();
                Map<String,String> map_body_timeline_unchecked = new HashMap<String,String>();
                Map<String,String> map_body_timeline_rejected = new HashMap<String,String>();
                Map<String,String> map_body_timeline_passed = new HashMap<String,String>();
                Map<String,String> map_body_timeline_deleted = new HashMap<String,String>();

                map_head_case.put("holder_type","1");
                map_head_case.put("text","案例");
                map_head_event.put("holder_type","1");
                map_head_event.put("text","事件");
                map_head_timeline.put("holder_type","1");
                map_head_timeline.put("text","时间线");

                map_body_case_unchecked.put("holder_type","2");
                map_body_case_unchecked.put("text","待审核");
                map_body_case_rejected.put("holder_type","2");
                map_body_case_rejected.put("text","未通过");
                map_body_case_passed.put("holder_type","2");
                map_body_case_passed.put("text","已通过");
                map_body_case_deleted.put("holder_type","2");
                map_body_case_deleted.put("text","已删除");

                map_body_event_unchecked.put("holder_type","2");
                map_body_event_unchecked.put("text","待审核");
                map_body_event_rejected.put("holder_type","2");
                map_body_event_rejected.put("text","未通过");
                map_body_event_passed.put("holder_type","2");
                map_body_event_passed.put("text","已通过");
                map_body_event_deleted.put("holder_type","2");
                map_body_event_deleted.put("text","已删除");

                map_body_timeline_unchecked.put("holder_type","2");
                map_body_timeline_unchecked.put("text","待审核");
                map_body_timeline_rejected.put("holder_type","2");
                map_body_timeline_rejected.put("text","未通过");
                map_body_timeline_passed.put("holder_type","2");
                map_body_timeline_passed.put("text","已通过");
                map_body_timeline_deleted.put("holder_type","2");
                map_body_timeline_deleted.put("text","已删除");
                
                arrayList.add(0,map_head_case);
                arrayList.add(1,map_body_case_rejected);
                arrayList.add(2,map_body_case_unchecked);
                arrayList.add(3,map_body_case_passed);
                arrayList.add(4,map_body_case_deleted);
                arrayList.add(5,map_head_event);
                arrayList.add(6,map_body_event_rejected);
                arrayList.add(7,map_body_event_unchecked);
                arrayList.add(8,map_body_event_passed);
                arrayList.add(9,map_body_event_deleted);
                arrayList.add(10,map_head_timeline);
                arrayList.add(11,map_body_timeline_rejected);
                arrayList.add(12,map_body_timeline_unchecked);
                arrayList.add(13,map_body_timeline_passed);
                arrayList.add(14,map_body_timeline_deleted);
                adaptor = new RecyclerViewAdaptor1(arrayList,getActivity(),"text");
                adaptor.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent;
                        if (position > 0 && position < 5) {
                            intent = new Intent(getActivity(),CaseListActivity.class);
                            intent.putExtra("entity_type",BaseActivity.ENTITY_CASE);
                        }else if (position > 5 && position < 10){
                            intent = new Intent(getActivity(),EventListActivity.class);
                            intent.putExtra("entity_type",BaseActivity.ENTITY_EVENT);
                        }else if (position > 10 && position < 15) {
                            intent = new Intent(getActivity(),TimelineListActivity.class);
                            intent.putExtra("entity_type",BaseActivity.ENTITY_TIMELINE);
                        } else {
                            intent = null;
                        }
                        if (intent != null) {
                            switch (position%5) {
                                case 1:
                                    intent.putExtra("req_type",BaseActivity.REJECTED);
                                    break;
                                case 2:
                                    intent.putExtra("req_type",BaseActivity.UNCHECKED);
                                    break;
                                case 3:
                                    intent.putExtra("req_type",BaseActivity.PASSED);
                                    break;
                                case 4:
                                    intent.putExtra("req_type",BaseActivity.DELETED);
                                    break;
                                default:break;
                            }
                            getActivity().startActivity(intent);
                        }
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adaptor);
                break;
            case 2:
                Map<String,String> map_head_company = new HashMap<String,String>();
                Map<String,String> map_body_company_unchecked = new HashMap<String,String>();
                Map<String,String> map_body_company_rejected = new HashMap<String,String>();
                Map<String,String> map_body_company_passed = new HashMap<String,String>();
                Map<String,String> map_body_company_deleted = new HashMap<String,String>();
                Map<String,String> map_head_product = new HashMap<String,String>();
                Map<String,String> map_body_product_unchecked = new HashMap<String,String>();
                Map<String,String> map_body_product_rejected = new HashMap<String,String>();
                Map<String,String> map_body_product_passed = new HashMap<String,String>();
                Map<String,String> map_body_product_deleted = new HashMap<String,String>();

                map_head_company.put("holder_type","1");
                map_head_company.put("text","公司");
                map_head_product.put("holder_type","1");
                map_head_product.put("text","产品");

                map_body_company_unchecked.put("holder_type","2");
                map_body_company_unchecked.put("text","待审核");
                map_body_company_rejected.put("holder_type","2");
                map_body_company_rejected.put("text","未通过");
                map_body_company_passed.put("holder_type","2");
                map_body_company_passed.put("text","已通过");
                map_body_company_deleted.put("holder_type","2");
                map_body_company_deleted.put("text","已删除");

                map_body_product_unchecked.put("holder_type","2");
                map_body_product_unchecked.put("text","待审核");
                map_body_product_rejected.put("holder_type","2");
                map_body_product_rejected.put("text","未通过");
                map_body_product_passed.put("holder_type","2");
                map_body_product_passed.put("text","已通过");
                map_body_product_deleted.put("holder_type","2");
                map_body_product_deleted.put("text","已删除");

                arrayList.add(0,map_head_company);
                arrayList.add(1,map_body_company_rejected);
                arrayList.add(2,map_body_company_unchecked);
                arrayList.add(3,map_body_company_passed);
                arrayList.add(4,map_body_company_deleted);
                arrayList.add(5,map_head_product);
                arrayList.add(6,map_body_product_rejected);
                arrayList.add(7,map_body_product_unchecked);
                arrayList.add(8,map_body_product_passed);
                arrayList.add(9,map_body_product_deleted);
                adaptor = new RecyclerViewAdaptor1(arrayList,getActivity(),"text");
                adaptor.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent;
                        if (position > 0 && position < 5) {
                            intent = new Intent(getActivity(),CompanyListActivity.class);
                            intent.putExtra("entity_type",BaseActivity.ENTITY_COMPANY);
                        }else{
                            intent = new Intent(getActivity(),ProductListActivity.class);
                            intent.putExtra("entity_type",BaseActivity.ENTITY_PRODUCT);
                        }
                        switch (position%5) {
                            case 1:
                                intent.putExtra("req_type",BaseActivity.REJECTED);
                                break;
                            case 2:
                                intent.putExtra("req_type",BaseActivity.UNCHECKED);
                                break;
                            case 3:
                                intent.putExtra("req_type",BaseActivity.PASSED);
                                break;
                            case 4:
                                intent.putExtra("req_type",BaseActivity.DELETED);
                                break;
                            default:break;
                        }
                        getActivity().startActivity(intent);
                    }
                });
                LinearLayoutManager linearLayoutManager_2 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager_2);
                recyclerView.setAdapter(adaptor);
                break;
            case 3:
                Map<String,String> map_head_admin = new HashMap<String,String>();
                Map<String,String> map_body_admin_passed = new HashMap<String,String>();
                Map<String,String> map_body_admin_locked = new HashMap<String,String>();
                Map<String,String> map_body_admin_deleted = new HashMap<String,String>();
                Map<String,String> map_head_user = new HashMap<String,String>();
                Map<String,String> map_body_user_passed = new HashMap<String,String>();
                Map<String,String> map_body_user_locked = new HashMap<String,String>();
                Map<String,String> map_body_user_deleted = new HashMap<String,String>();

                map_head_admin.put("holder_type","1");
                map_head_admin.put("text","管理员");
                map_head_user.put("holder_type","1");
                map_head_user.put("text","用户");

                map_body_admin_passed.put("holder_type","2");
                map_body_admin_passed.put("text","全部");
                map_body_admin_locked.put("holder_type","2");
                map_body_admin_locked.put("text","已锁定");
                map_body_admin_deleted.put("holder_type","2");
                map_body_admin_deleted.put("text","已删除");

                map_body_user_passed.put("holder_type","2");
                map_body_user_passed.put("text","已通过");
                map_body_user_locked.put("holder_type","2");
                map_body_user_locked.put("text","已锁定");
                map_body_user_deleted.put("holder_type","2");
                map_body_user_deleted.put("text","已删除");

                arrayList.add(0,map_head_admin);
                arrayList.add(1,map_body_admin_passed);
                arrayList.add(2,map_body_admin_locked);
                arrayList.add(3,map_body_admin_deleted);
                arrayList.add(4,map_head_user);
                arrayList.add(5,map_body_user_passed);
                arrayList.add(6,map_body_user_locked);
                arrayList.add(7,map_body_user_deleted);
                adaptor = new RecyclerViewAdaptor1(arrayList,getActivity(),"text");
                adaptor.setOnItemClickListener(new RecyclerViewAdaptor1.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent;
                        if (position > 0 && position < 4) {
                            intent = new Intent(getActivity(),AdminListActivity.class);
                            intent.putExtra("entity_type",BaseActivity.ENTITY_ADMIN);

                        }else {
                            intent = new Intent(getActivity(),UserListActivity.class);
                            intent.putExtra("entity_type",BaseActivity.ENTITY_USER);
                        }
                        switch (position%4) {
                            case 1:
                                intent.putExtra("req_type",BaseActivity.PASSED);
                                break;
                            case 2:
                                intent.putExtra("req_type",BaseActivity.LOCKED);
                                break;
                            case 3:
                                intent.putExtra("req_type",BaseActivity.DELETED);
                                break;
                            default:break;
                        }
                        getActivity().startActivity(intent);
                    }
                });
                LinearLayoutManager linearLayoutManager_3 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager_3);
                recyclerView.setAdapter(adaptor);
                break;
            default:break;
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
