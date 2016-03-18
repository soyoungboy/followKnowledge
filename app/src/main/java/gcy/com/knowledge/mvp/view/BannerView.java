package gcy.com.knowledge.mvp.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;

import gcy.com.knowledge.MainActivity;
import gcy.com.knowledge.R;
import gcy.com.knowledge.mvp.model.ZhihuTop;
import gcy.com.knowledge.utils.Constants;
import gcy.com.knowledge.utils.Imager;

/**
 * deals with displaying the top banner
 * convenientbanner的特定用法  内部调用这个Holder接口的实现类方法UpdateUI
 */
public class BannerView implements Holder<ZhihuTop> {
    private View view;

    @Override
    public View createView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.card_item_big, null);
        return view;
    }

    @Override
    public void UpdateUI(final Context context, int position, final ZhihuTop entity) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.story_img);
        TextView textView = (TextView) view.findViewById(R.id.story_title);
        Imager.load(context, entity.getImage(), imageView);
        textView.setText(entity.getTitle());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ZhihuDetailActivity.class);
                intent.putExtra(Constants.ID, entity.getId());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((MainActivity) context,
                        imageView, context.getString(R.string.shared_img));
                ActivityCompat.startActivity((MainActivity) context, intent, optionsCompat.toBundle());
            }
        });
    }
}