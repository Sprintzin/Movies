package com.gidi.movies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends
        RecyclerView.Adapter<Adapter.ViewHolder> {

    static final String TAG = "Adapter";
    private Context context;
    private static OnItemClickListener listener;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView year;
        public ImageView type_icon;
        public ImageView poster;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txt_title);
            year = (TextView) itemView.findViewById(R.id.txt_year);
            type_icon = (ImageView) itemView.findViewById(R.id.type_icon);
            poster = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }


    }


    private List<Movie> mList;

    public Adapter(Context context, List<Movie> mList) {
        this.mList = mList;
        this.context = context;

    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder
            (ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(movieView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder
            (Adapter.ViewHolder holder, int position) {
        Movie movie = mList.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText("(" + movie.getYear() + ")");

        if (movie.getType().equals("movie")) {
            holder.type_icon.setImageResource(R.drawable.film);

        } else if (movie.getType().equals("series")) {
            holder.type_icon.setImageResource(R.drawable.tv);

        } else if (movie.getType().equals("game")) {
            holder.type_icon.setImageResource(R.drawable.game);

        } else {
            holder.type_icon.setVisibility(View.INVISIBLE);

        }
        //Download image using picasso library
        Picasso.with(context).load(movie.getPoster())
                .error(R.drawable.search)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.search)
                .into(holder.poster);

//        (new AQuery(mContext))
//                .id(exploreViewHolder.getvProfilePic())
//                .image(item.getUserProfilePicUrl().trim(),
//                        true, true,
//                        device_width,
//                        R.drawable.profile_background,
//                        aquery.getCachedImage(R.drawable.profile_background),0);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // Define listener member variable
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
