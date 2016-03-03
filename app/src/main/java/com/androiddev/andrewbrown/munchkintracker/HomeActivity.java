package com.androiddev.andrewbrown.munchkintracker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int BASE_EXTRA_LEVELS = 0;
    private static final int BASE_LEVEL_LIMIT = 10;

    private static final String REMOVE_PLAYER = "Remove Player";
    private static final String PLAY_MODE = "Continue Game";

    private Context mContext;

    private Button mRemovePlayerButton;
    private Button mAddPlayerButton;
    private Button mSetLevelTo10;
    private Button mSetLevelTo11;
    private Button mSetLevelTo12;
    private ListView mPlayerListView;
    private PlayerAdapter mPlayerAdapter;
    private TextView mPlayToLimitTextView;
    private ToggleButton mNormalEpicGameToggle;

    private int mExtraLevels = 0;
    private int mPlayToLevel = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {
        mContext = getApplicationContext();
        mAddPlayerButton = (Button) findViewById(R.id.home_button_addplayer);
        mAddPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPlayerDialog();
            }
        });
        mRemovePlayerButton = (Button) findViewById(R.id.home_button_removeplayer);
        mRemovePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean currentlyInEditMode = mPlayerAdapter.isInEditMode();
                mPlayerAdapter.setEditMode(!currentlyInEditMode);
                if (!currentlyInEditMode) {
                    mRemovePlayerButton.setText(PLAY_MODE);
                    enableGameMode(false);
                } else {
                    mRemovePlayerButton.setText(REMOVE_PLAYER);
                    enableGameMode(true);
                }
            }
        });
        mPlayerListView = (ListView) findViewById(R.id.home_listview_playerlist);
        mPlayerAdapter = new PlayerAdapter(mContext, new CheckForWinListener() {
            @Override
            public void checkForWin(Player player) {
                if (player.level == (mPlayToLevel+mExtraLevels)) {
                    announceWinner(player.name);
                }
            }
        });

        mPlayerListView.setAdapter(mPlayerAdapter);

        mSetLevelTo10 = (Button) findViewById(R.id.home_button_playto10);
        mSetLevelTo10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExtraLevelLimit(0);
            }
        });

        mSetLevelTo11 = (Button) findViewById(R.id.home_button_playto11);
        mSetLevelTo11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExtraLevelLimit(1);
            }
        });

        mSetLevelTo12 = (Button) findViewById(R.id.home_button_playto12);
        mSetLevelTo12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExtraLevelLimit(2);
            }
        });

        mPlayToLimitTextView = (TextView) findViewById(R.id.home_textview_playtolevel);

        mNormalEpicGameToggle = (ToggleButton) findViewById(R.id.home_togglebutton_normalorepic);
        mNormalEpicGameToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    setLevelLimit(BASE_LEVEL_LIMIT);
                } else {
                    setLevelLimit(2 * BASE_LEVEL_LIMIT);
                }
            }
        });

    }

    private void enableGameMode(boolean enabled) {
        mAddPlayerButton.setEnabled(enabled);
        mSetLevelTo10.setEnabled(enabled);
        mSetLevelTo11.setEnabled(enabled);
        mSetLevelTo12.setEnabled(enabled);
        mNormalEpicGameToggle.setEnabled(enabled);
    }

    private void setExtraLevelLimit(int extraLevels) {
        mExtraLevels = extraLevels;
        setPlayToLevel();
    }

    private void setLevelLimit(int levelLimit) {
        mPlayToLevel = levelLimit;
        setPlayToLevel();
    }

    private void setPlayToLevel() {
        mPlayToLimitTextView.setText(Integer.toString(getTotalWinLevel()));
        checkForWinners();
    }

    private void checkForWinners() {
        List<String> winners = new ArrayList<String>();
        for (int x=0; x<mPlayerAdapter.getCount(); x++ ) {
            Player player = mPlayerAdapter.getItem(x);
            if (player.level >= getTotalWinLevel()) {
                winners.add(player.name);
            }
        }
        if (winners.size()>0) {
            String winnersString = "";
            for (int i=0; i<winners.size(); i++) {
                winnersString = winnersString+winners.get(i);
                if (i<=(winners.size()-3)) {
                    winnersString = winnersString+", ";
                } else if (i<= winners.size()-2) {
                    winnersString = winnersString+" and ";
                }
            }
            announceWinner(winnersString);
        }
    }

    private void showAddPlayerDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Player")
                .setMessage("Enter Player Name:")
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final EditText playerName = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        playerName.setLayoutParams(lp);
        alertDialog.setView(playerName);

        final AlertDialog dialog = alertDialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = playerName.getText().toString();
                if (!name.equals("")) {
                    Player newPlayer = new Player(playerName.getText().toString());
                    addPlayerToList(newPlayer);
                    dialog.dismiss();
                } else {
                    Toast.makeText(mContext, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void announceWinner(String winner) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("CONGRATULATIONS")
                .setMessage("Congratulations "+winner+", " +
                        "you have won by reaching level "+getTotalWinLevel()+" first")
                .setPositiveButton("Continue & Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int x = 0; x < mPlayerAdapter.getCount(); x++) {
                            Player player = mPlayerAdapter.getItem(x);
                            player.reset();
                        }
                        mPlayerAdapter.notifyDataSetChanged();
                        resetLevelLimit();
                    }
                });
        alertDialog.show();
    }

    private void resetLevelLimit() {
        mPlayToLevel = BASE_LEVEL_LIMIT;
        mExtraLevels = BASE_EXTRA_LEVELS;
        mPlayToLimitTextView.setText(Integer.toString(getTotalWinLevel()));
    }

    private int getTotalWinLevel() {
        return mPlayToLevel + mExtraLevels;
    }

    private void addPlayerToList(Player player) {
        mPlayerAdapter.add(player);
        mPlayerAdapter.notifyDataSetChanged();
    }

    public interface CheckForWinListener {

        void checkForWin(Player player);
    }
}
