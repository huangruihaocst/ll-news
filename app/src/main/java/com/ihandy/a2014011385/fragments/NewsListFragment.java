package com.ihandy.a2014011385.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.Toast;

import com.ihandy.a2014011385.R;
import com.ihandy.a2014011385.adapters.NewsRecyclerAdapter;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CATEGORY = "category";

    // TODO: Rename and change types of parameters
    private String category;

    private OnFragmentInteractionListener mListener;

    private final String NEWS_LIST_FRAGMENT_TAG = "NewsListFragment";

    private final int GET_NEWS_MESSAGE_WHAT = 0;
    private final int GET_MORE_NEWS_MESSAGE_WHAT = 1;

    /**
     * Handle UI conduction when a message comes.
     */
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            // TODO: add real handling of message
            switch (message.what) {
                case GET_NEWS_MESSAGE_WHAT:
                    Toast.makeText(getContext(), "get news done", Toast.LENGTH_LONG).show();
                    break;
                case GET_MORE_NEWS_MESSAGE_WHAT:
                    Toast.makeText(getContext(), "get more news done", Toast.LENGTH_LONG).show();
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
     * @param category The category of the news list.
     * @return A new instance of fragment NewsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsListFragment newInstance(String category) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_news_list, container, false);
        RecyclerView newsRecyclerView  = (RecyclerView) root.findViewById(R.id.list);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DataAccessor accessor = DataAccessor.getInstance();
        accessor.setContext(getContext());
        ArrayList<News> newsArrayList = accessor.getNewsArrayList(category); // in new thread
        newsRecyclerView.setAdapter(new NewsRecyclerAdapter(getContext(), newsArrayList)); // in handler
        GetNewsThread thread = new GetNewsThread();
        thread.start();
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

    class GetNewsThread extends Thread {
        @Override
        public void run() {
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.e(NEWS_LIST_FRAGMENT_TAG, e.getMessage());
            }
            Message getNewsDoneMessage = new Message();
            getNewsDoneMessage.what = GET_NEWS_MESSAGE_WHAT;
            handler.sendMessage(getNewsDoneMessage);
        }
    }
}
