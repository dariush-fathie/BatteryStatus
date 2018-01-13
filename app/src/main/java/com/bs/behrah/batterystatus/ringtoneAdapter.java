package com.bs.behrah.batterystatus;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class ringtoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> buffer = new ArrayList<>();
    Context context;
    private SharedPre shp;

    private MediaPlayer mp = new MediaPlayer();

    MediaPlayer getMp() {
        return mp;
    }

    private void setMp(MediaPlayer mp) {
        this.mp = mp;
    }


    private ArrayList<Boolean> chechedArr = new ArrayList<>();

    ringtoneAdapter(Context context) {
        this.context = context;
        shp = new SharedPre(context);
        listRingtone_mth();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.ringtone_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ViewHolder) holder).title_ringtone_tv.setText(buffer.get(position).substring(0, buffer.get(position).length() - 4));

        if (!chechedArr.get(position)) {
            ((ViewHolder) holder).radio_iv.setImageResource(R.drawable.radio_iv_empty_tag);
        } else {
            ((ViewHolder) holder).radio_iv.setImageResource(R.drawable.radio_iv_fill_tag);
        }
    }

    @Override
    public int getItemCount() {
        Log.e("buffer", buffer.size() + "");
        return buffer.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title_ringtone_tv;
        ImageView radio_iv;

        ViewHolder(View itemView) {
            super(itemView);

            title_ringtone_tv = (TextView) itemView.findViewById(R.id.title_ringtone_tv);
            radio_iv = (ImageView) itemView.findViewById(R.id.radio_iv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cheched_mth(getAdapterPosition());
        }

    }

    private void listRingtone_mth() {

        File myfile = new File("/system/media/audio/ringtones/");
        File listfile[] = myfile.listFiles();

        chechedArr.add(false);
        buffer.add("Default (پیش فرض)" + ".mp3");

        for (File aListfile : listfile) {

            buffer.add(aListfile.getName());
            chechedArr.add(false);
        }
    }

    private void cheched_mth(int k) {

        if (k != 0) {
            shp.setRingtone(buffer.get(k));
        } else {
            shp.setRingtone("پیش فرض");
        }

        if (!chechedArr.get(k)) {
            chechedArr.set(k, true);

            notifyItemChanged(k);

            for (int i = 0; i < chechedArr.size(); i++) {
                if (i != k && chechedArr.get(i)) {
                    chechedArr.set(i, false);
                    notifyItemChanged(i);
                }
            }

            play();
        }else {
            play();
        }

    }

    private void play() {

        if (getMp().isPlaying()) {
            getMp().stop();
        }
        if (getMp() != null) {
            getMp().release();
            setMp(null);
        }
        if (shp.getRingtone().equals("پیش فرض")) {
            setMp(MediaPlayer.create(context, R.raw.sharj));
        } else {
            Uri uri = Uri.parse("/system/media/audio/ringtones/" + shp.getRingtone());
            setMp(MediaPlayer.create(context, uri));
        }

        getMp().start();
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 8, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}
