package com.ihandy.a2014011385.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.Toast;

import com.ihandy.a2014011385.R;
import com.ihandy.a2014011385.adapters.NewsRecyclerAdapter;
import com.ihandy.a2014011385.helpers.CallBack;
import com.ihandy.a2014011385.helpers.DataAccessor;
import com.ihandy.a2014011385.helpers.News;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CATEGORY_NAME = "categoryName";
    private static final String FILTERS = "filters";

    private String categoryName;
    private ArrayList<String> filters;
    private boolean isLoading = false;
    private boolean noMore = false;
    private int visibleThreshold = 2;
    private int newsListLength = 0;

    private OnFragmentInteractionListener mListener;

    private NewsRecyclerAdapter adapter;

    private final String NEWS_LIST_FRAGMENT_TAG = "NewsListFragment";

    private final int GET_NEWS_LIST_MESSAGE_WHAT = 0;
    private final int REFRESH_MESSAGE_WHAT = 1;
    private final int GET_MORE_NEWS_MESSAGE_WHAT = 2;

    private RecyclerView newsRecyclerView;
    private ArrayList<News> newsArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Handle UI conduction when a message comes.
     */
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case GET_NEWS_LIST_MESSAGE_WHAT:
                    adapter = new NewsRecyclerAdapter(getContext(), newsArrayList, filters);
                    newsRecyclerView.setAdapter(adapter);
                    newsListLength = newsArrayList.size();
                    break;
                case REFRESH_MESSAGE_WHAT:
                    adapter = new NewsRecyclerAdapter(getContext(), newsArrayList, filters);
                    newsRecyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    newsListLength = newsArrayList.size();
                    break;
                case GET_MORE_NEWS_MESSAGE_WHAT:
                    adapter.notifyItemInserted(newsListLength - 1);
                    adapter.notifyDataSetChanged();
                    newsListLength = newsArrayList.size();
                    isLoading = false;
                    break;
                default:
                    // nothing will be done
            }
        }
    };

    public NewsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category The categoryName of the news list.
     * @return A new instance of fragment NewsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsListFragment newInstance(String category, ArrayList<String> filters) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_NAME, category);
        args.putStringArrayList(FILTERS, filters);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString(CATEGORY_NAME);
            filters = getArguments().getStringArrayList(FILTERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_news_list, container, false);
        newsRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        newsRecyclerView.setLayoutManager(layoutManager);

        final DataAccessor accessor = DataAccessor.getInstance(); // get text information for each item
        accessor.setContext(getContext());
        accessor.getNewsList(categoryName, new CallBack<ArrayList<News>>() {
            @Override
            public void onCallBack(ArrayList<News> response) {
                if (response == null) {
                    // accessing failed, nothing done
                } else {
                    newsArrayList = response;
                    Message message = new Message();
                    message.what = GET_NEWS_LIST_MESSAGE_WHAT;
                    handler.sendMessage(message);
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                accessor.forceAccessFromInternet(categoryName, new CallBack<ArrayList<News>>() {
                    @Override
                    public void onCallBack(ArrayList<News> response) {
                        if (response == null) { // refreshing failed, simply stop button spinning
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            newsArrayList = response;
                            Message message = new Message();
                            message.what = REFRESH_MESSAGE_WHAT;
                            handler.sendMessage(message);
                            Toast.makeText(getContext(), getString(R.string.refresh_success), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        newsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!isLoading && !noMore && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    accessor.getMoreNews(categoryName, new CallBack<ArrayList<News>>() {
                        @Override
                        public void onCallBack(ArrayList<News> response) {
                            if (response == null) {
                                // access failed, stop loading and allow a second try
                                isLoading = false;
                            } else {
                                if (response.size() == newsArrayList.size()) { // nothing news
                                    noMore = true;
                                } else {
                                    newsArrayList = response;
                                    Message message = new Message();
                                    message.what = GET_MORE_NEWS_MESSAGE_WHAT;
                                    handler.sendMessage(message);
                                }
                            }
                        }
                    });
                    isLoading = true;
                }
            }
        });

        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setFilters(ArrayList<String> filters) {
        if (filters != null) {
            this.filters = new ArrayList<>();
            for (String filter: filters) {
                this.filters.add(filter);
            }
        }
        if (adapter != null) { // fragments might have not been loaded, thus adapter is null
            adapter.setFilters(filters);
        }
    }
}
