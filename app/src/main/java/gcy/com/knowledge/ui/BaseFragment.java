package gcy.com.knowledge.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.nio.Buffer;
import java.util.zip.Inflater;

import butterknife.ButterKnife;
import gcy.com.knowledge.R;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {
    public View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView==null){
            int layoutId=initLayoutId();
            rootView= inflater.inflate(layoutId,container,false);
            ButterKnife.bind(this,rootView);
            initViews();
        }
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected abstract int initLayoutId();
    protected abstract void initViews();
    protected abstract void initData();
    public boolean isAlive() {
        return getActivity() != null;
    }
}
