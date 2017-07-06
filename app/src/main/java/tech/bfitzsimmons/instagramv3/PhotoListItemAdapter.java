package tech.bfitzsimmons.instagramv3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brian on 7/4/2017.
 */

public class PhotoListItemAdapter extends RecyclerView.Adapter<PhotoListItemAdapter.ViewHolder> {
    private List<PhotoListItem> photoListItems;

    @Override
    public PhotoListItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
        return new PhotoListItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoListItemAdapter.ViewHolder holder, int position) {
        PhotoListItem photoListItem = photoListItems.get(position);
        holder.imageView.setImageBitmap(photoListItem.getImageBitmap());
        holder.createdAt.setText(photoListItem.getCreatedAt());
        holder.caption.setText(photoListItem.getCaption());
    }

    @Override
    public int getItemCount() {
        return photoListItems.size();
    }

    public PhotoListItemAdapter(List<PhotoListItem> photoListItems) {
        this.photoListItems = photoListItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView createdAt;
        private TextView caption;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photoImageView);
            createdAt = (TextView) itemView.findViewById(R.id.photoCreatedAt);
            caption = (TextView) itemView.findViewById(R.id.caption);
        }
    }
}
