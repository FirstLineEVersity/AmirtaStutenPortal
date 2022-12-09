package com.amirta.studentinformation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.recyclerview.widget.RecyclerView;

import com.amirta.studentinformation.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    List<YouTubeVideos> youTubeVideosList;

    public VideoAdapter(){
    }

    public VideoAdapter(List<YouTubeVideos> youtubeVideoList){
        this.youTubeVideosList = youtubeVideoList;
    }

    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videoview,parent,false);
        return new VideoViewHolder(view);
    }

    public void onBindViewHolder(VideoViewHolder holder,int position){
        //holder.videoWeb.loadData(youTubeVideosList.get(position).getVideoUrl(),"text/html", "utf-8");
        holder.videoWeb.loadDataWithBaseURL("https://www.youtube.com",youTubeVideosList.get(position).getVideoUrl(),"text/html", "utf-8",null);
        //holder.videoWeb.loadUrl(youTubeVideosList.get(position).getVideoUrl());
    }

    public int getItemCount(){return youTubeVideosList.size();}

    public class VideoViewHolder extends RecyclerView.ViewHolder{
        WebView videoWeb;

        public VideoViewHolder(View itemView){
            super (itemView);
            videoWeb = itemView.findViewById(R.id.videoWebView);
            videoWeb.getSettings().setJavaScriptEnabled(true);
            videoWeb.loadUrl("http://www.youtube.com/");
            videoWeb.setWebChromeClient(new WebChromeClient(){

            });

            videoWeb.getSettings().setBuiltInZoomControls(true);
            videoWeb.getSettings().setSupportZoom(true);
            videoWeb.getSettings().setUseWideViewPort(true);
            videoWeb.getSettings().setLoadWithOverviewMode(true);
        }
    }
}
