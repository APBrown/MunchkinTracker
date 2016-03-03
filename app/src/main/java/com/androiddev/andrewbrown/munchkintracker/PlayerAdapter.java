package com.androiddev.andrewbrown.munchkintracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * TODO: javadoc
 *
 * @author andrew.brown@laterooms.com
 */
public class PlayerAdapter extends ArrayAdapter<Player> {

    private HomeActivity.CheckForWinListener listener;
    private boolean isEditMode = false;

    public PlayerAdapter(Context context, HomeActivity.CheckForWinListener listener) {
        super(context, -1);
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Player player = getItem(position);
        final PlayerViewHolder viewHolder = new PlayerViewHolder();

        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_item, parent, false);
        }

        viewHolder.playerName = (TextView) convertView.findViewById(R.id.playeritem_textview_playername);
        viewHolder.playerLevel = (TextView) convertView.findViewById(R.id.playeritem_textview_level);
        viewHolder.addLevel = (Button) convertView.findViewById(R.id.playeritem_button_addlevel);
        viewHolder.removeLevel = (Button) convertView.findViewById(R.id.playeritem_button_removelevel);
        viewHolder.removePlayer = (Button) convertView.findViewById(R.id.playeritem_button_removePlayer);

        viewHolder.playerName.setText(player.name);
        viewHolder.playerLevel.setText(player.getLevel());

        viewHolder.addLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.addLevel();
                viewHolder.playerLevel.setText(player.getLevel());
                listener.checkForWin(player);
            }
        });

        viewHolder.removeLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.removeLevel();
                viewHolder.playerLevel.setText(player.getLevel());
            }
        });

        viewHolder.removePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlayer(position);
            }
        });

        setViewsForEditMode(viewHolder);

        return convertView;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        notifyDataSetChanged();
    }

    public boolean isInEditMode() {
        return isEditMode;
    }

    private void setViewsForEditMode(PlayerViewHolder viewHolder) {
        if (isEditMode) {
            viewHolder.removePlayer.setVisibility(View.VISIBLE);
            viewHolder.addLevel.setVisibility(View.INVISIBLE);
            viewHolder.removeLevel.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.removePlayer.setVisibility(View.INVISIBLE);
            viewHolder.addLevel.setVisibility(View.VISIBLE);
            viewHolder.removeLevel.setVisibility(View.VISIBLE);
        }
    }

    private void removePlayer(int player) {
        remove(getItem(player));
        notifyDataSetChanged();
    }

    private static class PlayerViewHolder {
        public TextView playerName;
        public TextView playerLevel;
        public Button addLevel;
        public Button removeLevel;
        public Button removePlayer;
    }
}
