package gcy.com.knowledge.mvp.other;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import butterknife.ButterKnife;
import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.interf.OnListFragmentInteract;
import gcy.com.knowledge.mvp.model.ZhihuJson;
import gcy.com.knowledge.mvp.model.ZhihuStory;
import gcy.com.knowledge.mvp.model.ZhihuTop;
import gcy.com.knowledge.mvp.view.BannerView;
import gcy.com.knowledge.net.API;
import gcy.com.knowledge.net.DB;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.Dater;
import gcy.com.knowledge.utils.Imager;
import gcy.com.knowledge.utils.SPUtil;

/**
 * ============================================================
 * <p/>
 * 版     权 ： keyboard3 所有
 * <p/>
 * 作     者  :  甘春雨
 * <p/>
 * 版     本 ： 1.0
 * <p/>
 * 创 建日期 ： 2016/3/5
 * <p/>
 * 描     述 ：
 * <p/>
 * <p/>
 * 修 订 历史：
 * <p/>
 * ============================================================
 */
public class ZhihuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BANNER = 0;
    /**
     * header is a title to display date
     */
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    /**
     * footer is to show load more hint
     */
    private static final int TYPE_FOOTER = 3;
    public static int textGrey;
    public static int textDark;
    //数据集组成
    private List<ZhihuStory> zhihuStories;
    private List<ZhihuTop> tops;
    private OnListFragmentInteract listener;
    public ZhihuListAdapter(OnListFragmentInteract listener){
        this.listener=listener;
        zhihuStories= DB.findAll(ZhihuStory.class);
        tops=DB.findAll(ZhihuTop.class);
    }

    public void addNews(ZhihuJson news) {
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=null;
        if(viewType==TYPE_BANNER){
            view = inflater.inflate(R.layout.fragment_news_banner, parent, false);
            return new BannerViewHolder(view);
        }
        else if(viewType==TYPE_FOOTER){
            view = inflater.inflate(R.layout.footer_loading, parent, false);
            return new FooterViewHolder(view);
        }else{
            view = inflater.inflate(R.layout.fragment_news_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_BANNER;
        }else if(position==getItemCount()-1){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Context context=holder.itemView.getContext();
        //先设置文本颜色值
        textGrey = ContextCompat.getColor(context, R.color.darker_gray);
        textDark = ContextCompat.getColor(context, android.support.design.R.color.abc_primary_text_material_light);

        if(holder instanceof ViewHolder){
            final ViewHolder viewHolder=(ViewHolder)holder;

            if(position==1){
                viewHolder.header.setText("今日新闻");
                viewHolder.header.setVisibility(View.VISIBLE);
                viewHolder.mItem.setVisibility(View.GONE);
                return;
            }

            viewHolder.zhihuStory=zhihuStories.get(position-2);//position=0, 1 are occupied with banner, header
            //type == 1 means this item is added by me, so it's a header to show date.
            if (viewHolder.zhihuStory.getType() == 1) {
                String date = Dater.getDisplayDate(viewHolder.zhihuStory.getId() + "");
                viewHolder.header.setText(date);
                viewHolder.header.setVisibility(View.VISIBLE);
                viewHolder.header.setClickable(false);
                viewHolder.mItem.setVisibility(View.GONE);
                return;
            } else {
                viewHolder.header.setVisibility(View.GONE);
                viewHolder.mItem.setVisibility(View.VISIBLE);
            }
            Imager.load(context, viewHolder.zhihuStory.getImages().get(0).getVal(), viewHolder.mImage);
            viewHolder.mTitle.setText(viewHolder.zhihuStory.getTitle());
            viewHolder.mTitle.setTextColor(textDark);
            //添加点击事件
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onListFragmentInteraction(viewHolder);
                }
            });
        }else if(holder instanceof BannerViewHolder){
            final BannerViewHolder viewHolder=(BannerViewHolder)holder;
            viewHolder.banner.setPages(new CBViewHolderCreator(){

                @Override
                public Object createHolder() {
                    return new BannerView();
                }
            },tops);
        }
    }

    @Override
    public int getItemCount() {
        //Banner+footerView+2
        return zhihuStories.size()+2;
    }
    public class FooterViewHolder extends RecyclerView.ViewHolder{

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView header;
        public final ImageView mImage;
        public final TextView mTitle;
        public final View mItem;
        public ZhihuStory zhihuStory;

        public ViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.story_header);
            mImage = (ImageView) view.findViewById(R.id.story_img);
            mTitle = (TextView) view.findViewById(R.id.story_title);
            mItem = view.findViewById(R.id.story_item);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitle.getText() + "'";
        }
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        public final ConvenientBanner<ZhihuTop> banner;
        public BannerViewHolder(View view) {
            super(view);
            banner= (ConvenientBanner) view.findViewById(R.id.convenientBanner);
        }
    }
}
