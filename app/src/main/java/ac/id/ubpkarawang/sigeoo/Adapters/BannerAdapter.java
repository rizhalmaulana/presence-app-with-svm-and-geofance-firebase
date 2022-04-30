package ac.id.ubpkarawang.sigeoo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.Banner;
import ac.id.ubpkarawang.sigeoo.R;
import ac.id.ubpkarawang.sigeoo.Utils.Static;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {

    ArrayList<Banner> semuaBanner;

    public BannerAdapter(Context context, ArrayList<Banner> bannerItem) {
        this.semuaBanner = bannerItem;
    }

    @NonNull
    @Override
    public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_banner, parent, false);
        return new BannerAdapter.BannerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerHolder holder, int position) {
        String fotoBanner = Static.STORAGE_BANNER_API + semuaBanner.get(position).getFoto_banner();
        Picasso.get().load(fotoBanner)
                .centerCrop()
                .placeholder(R.drawable.item_background)
                .fit()
                .into(holder.item_banner);
    }

    @Override
    public int getItemCount() {
        return semuaBanner.size();
    }

    public class BannerHolder extends RecyclerView.ViewHolder {
        ImageView item_banner;

        public BannerHolder(@NonNull View itemView) {
            super(itemView);
            item_banner = itemView.findViewById(R.id.img_item_banner);
        }
    }
}
