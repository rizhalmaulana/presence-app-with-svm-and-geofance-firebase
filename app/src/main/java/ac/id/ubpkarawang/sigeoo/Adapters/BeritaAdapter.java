package ac.id.ubpkarawang.sigeoo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ac.id.ubpkarawang.sigeoo.Model.Informasi.Berita;
import ac.id.ubpkarawang.sigeoo.R;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.BeritaHolder>{

    private ArrayList<Berita> semuaBerita;

    public BeritaAdapter(Context mContext, ArrayList<Berita> berita) {
        this.semuaBerita = berita;
    }

    public void setBeritaList(ArrayList<Berita> beritaList) {
        this.semuaBerita = beritaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BeritaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_informasi, parent, false);
        return new BeritaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeritaHolder holder, int position) {
        holder.judulBerita.setText(semuaBerita.get(position).getTitle());
        holder.ketBerita.setText(semuaBerita.get(position).getDescription());
        holder.tglBerita.setText(semuaBerita.get(position).getPublishedAt());

        String foto = semuaBerita.get(position).getUrlToImage();
        Picasso.get().load(foto).into(holder.fotoBerita);
    }

    @Override
    public int getItemCount() {
        return semuaBerita.size();
    }

    public void refill(ArrayList<Berita> semuaBerita) {
        this.semuaBerita = new ArrayList<>();
        this.semuaBerita.addAll(semuaBerita);
    }

    static class BeritaHolder extends RecyclerView.ViewHolder {
        ImageView fotoBerita;
        TextView judulBerita, ketBerita, tglBerita;

        BeritaHolder(@NonNull View itemView) {
            super(itemView);

            fotoBerita = itemView.findViewById(R.id.item_gambar_edukasi);
            judulBerita = itemView.findViewById(R.id.item_nama_edukasi);
            ketBerita = itemView.findViewById(R.id.item_keterangan_edukasi);
            tglBerita = itemView.findViewById(R.id.item_tanggal_edukasi);
        }
    }
}
