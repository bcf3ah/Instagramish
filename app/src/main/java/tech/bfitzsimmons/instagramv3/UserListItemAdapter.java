package tech.bfitzsimmons.instagramv3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brian on 7/4/2017.
 */

public class UserListItemAdapter extends RecyclerView.Adapter<UserListItemAdapter.ViewHolder> {

    private List<UserListItem> userListItems;

    @Override
    public UserListItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListItemAdapter.ViewHolder holder, int position) {
        final UserListItem user = userListItems.get(position);
        final String username = user.getUsername();
        holder.createdAt.setText(user.getCreatedAt());
        holder.username.setText(username);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                Intent intent = new Intent(v.getContext(), PhotoListActivity.class);
                intent.putExtra("username", username);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userListItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username, createdAt;

        public ViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            createdAt = (TextView) itemView.findViewById(R.id.createdAt);
        }
    }

    public UserListItemAdapter(List<UserListItem> userListItems) {
        this.userListItems = userListItems;
    }
}
