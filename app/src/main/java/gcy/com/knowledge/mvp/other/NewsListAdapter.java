package gcy.com.knowledge.mvp.other;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.interf.OnListFragmentInteract;
import gcy.com.knowledge.mvp.model.FreshJson;
import gcy.com.knowledge.mvp.model.FreshPost;
import gcy.com.knowledge.net.DB;
import gcy.com.knowledge.utils.Imager;

/**
 * ============================================================
 * <p>
 * 版     权 ： keyboard3 所有
 * <p>
 * 作     者  :  甘春雨
 * <p>
 * 版     本 ： 1.0
 * <p>
 * 创 建日期 ： 2016/3/5
 * <p>
 * 描     述 ：
 * <p>
 * <p>
 * 修 订 历史：
 * <p>
 * ============================================================
 */
public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FreshPost> freshPosts;
    private OnListFragmentInteract mListener;

    public NewsListAdapter(OnListFragmentInteract listener) {
        mListener = listener;
        freshPosts = DB.findAllDateSorted(FreshPost.class);
    }

    public void addNews(FreshJson news) {
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_fresh_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.freshPost = freshPosts.get(position);
        String imgUrl = viewHolder.freshPost.getCustom_fields().getThumb_c().get(0).getVal();

        viewHolder.mTitle.setText(viewHolder.freshPost.getTitle());
        viewHolder.mTitle.setTextColor(ZhihuListAdapter.textDark);
        Imager.load(viewHolder.itemView.getContext(), imgUrl, viewHolder.mImage);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(viewHolder);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return freshPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImage;
        public final TextView mTitle;
        public final View mItem;
        public FreshPost freshPost;

        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.story_img);
            mTitle = (TextView) view.findViewById(R.id.story_title);
            mItem = view.findViewById(R.id.story_item);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }
}