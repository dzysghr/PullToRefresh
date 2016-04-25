package com.dzy.pulltorefresh;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class BlankFragment extends Fragment
{

    private static final String ARG_PARAM1 = "param1";


    private String mParam1;

    public BlankFragment()
    {

    }


    public static BlankFragment newInstance(String param1)
    {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view =inflater.inflate(R.layout.fragment_blank, container, false);

        TextView tv = (TextView) view.findViewById(R.id.tv);
        tv.setText(mParam1);
        return view;
    }



    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }



}
