package space.weme.remix.ui.community;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.Commit;
import space.weme.remix.model.Post;
import space.weme.remix.model.Reply;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.GridLayout;

/**
 * Created by Liujilong on 2016/1/30.
 * liujilong.me@gmail.com
 */
public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;

    Post mPost;
    List<Reply> mReplyList;

    View.OnClickListener mListener;

    private static final int VIEW_TITLE = 1;
    private static final int VIEW_ITEM = 2;
    private static final int VIEW_PROGRESS = 3;


    PostAdapter(Context context){
        mContext = context;
        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    void setPost(Post post){
        mPost = post;
    }
    void setReplyList(List<Reply> list){
        mReplyList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == VIEW_TITLE){
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_post_title,parent,false);
            holder = new PostViewHolder(v);
        }else if(viewType == VIEW_ITEM){
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_post_reply,parent,false);
            holder = new ItemViewHolder(v);
        }else{
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_post_progress,parent,false);
            holder = new ProgressViewHolder(v);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PostViewHolder){
            if(mPost==null) { return; }
            PostViewHolder viewHolder = (PostViewHolder) holder;
            viewHolder.avatarDraw.setImageURI(Uri.parse(StrUtils.thumForID(mPost.userId)));
            // todo listener
            viewHolder.tvName.setText(mPost.name);
            viewHolder.tvUniversity.setText(mPost.school);
            viewHolder.tvTime.setText(mPost.timestamp);
            viewHolder.tvTitle.setText(mPost.title);
            viewHolder.tvContent.setText(mPost.body);
            viewHolder.imagesGridLayout.removeAllViews();
            for(String url : mPost.imageUrl) {
                SimpleDraweeView image = new SimpleDraweeView(mContext);
                viewHolder.imagesGridLayout.addView(image);
                image.setImageURI(Uri.parse(url));
                image.setTag(url);
                image.setOnClickListener(mListener);
                // TODO implement listener
            }
            viewHolder.tvLikeNumber.setText(mPost.likenumber);
            viewHolder.tvCommit.setText(mPost.commentnumber);
            if(mPost.flag.equals("0")){
                viewHolder.ivLike.setImageResource(R.mipmap.like_off);
                viewHolder.likeLayout.setOnClickListener(mListener);
                // TODO implement
            }else{
                viewHolder.ivLike.setImageResource(R.mipmap.like_on);
            }
            viewHolder.commitLayout.setOnClickListener(mListener); // TODO implement
            viewHolder.llLikePeoples.removeAllViews();
            for(int id : mPost.likeusers){
                SimpleDraweeView avatar = (SimpleDraweeView) LayoutInflater.from(mContext).
                        inflate(R.layout.aty_post_avatar,viewHolder.llLikePeoples,false);
                viewHolder.llLikePeoples.addView(avatar);
                avatar.setImageURI(Uri.parse(StrUtils.thumForID(Integer.toString(id))));
                avatar.setTag(id);
                avatar.setOnClickListener(mListener);//todo implement
                // todo show more
            }
        }else if(holder instanceof ItemViewHolder){
            final Reply reply = mReplyList.get(position-1);
            if(reply == null){return;}
            final ItemViewHolder item = (ItemViewHolder) holder;
            item.avatarDraw.setImageURI(Uri.parse(StrUtils.thumForID(reply.userid)));
            item.avatarDraw.setTag(reply.userid);
            item.avatarDraw.setOnClickListener(mListener);
            item.tvName.setText(reply.name);
            item.tvUniversity.setText(reply.school);
            item.tvTime.setText(reply.timestamp);
            item.tvContent.setText(reply.body);
            item.tvLike.setText(String.format("%d", reply.likenumber));
            item.tvCommit.setText(String.format("%d", reply.commentnumber));
            if(reply.flag.equals("0")){
                item.ivLike.setImageResource(R.mipmap.like_off);
                item.ivLike.setOnClickListener(mListener);// todo listener
            }else{
                item.ivLike.setImageResource(R.mipmap.like_on);
            }
            item.llCommit.setTag(reply);
            item.llCommit.setOnClickListener(mListener); // todo listener
            item.llReplyList.removeAllViews();
            item.llReplyList.setVisibility(reply.reply.size()==0?View.GONE:View.VISIBLE);
            for(Commit commitReply : reply.reply){
                TextView tv = new TextView(mContext);
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(commitReply.name);
                int color = mContext.getResources().getColor(R.color.colorPrimary);
                builder.setSpan(new ForegroundColorSpan(color), 0, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if(commitReply.destcommentid!=null){
                    builder.append(mContext.getString(R.string.reply));
                    int len = builder.length();
                    builder.append(commitReply.destname);
                    builder.setSpan(new ForegroundColorSpan(color),len,builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                builder.append(":").append(commitReply.body);
                tv.setText(builder);
                item.llReplyList.addView(tv);
            }
            item.imagesGridLayout.removeAllViews();
            if(reply.image==null || reply.image.size()==0){
                item.imagesGridLayout.setVisibility(View.GONE);
            }else {
                item.imagesGridLayout.setVisibility(View.VISIBLE);
                for (String url : reply.image) {
                    SimpleDraweeView drawView = new SimpleDraweeView(mContext);
                    drawView.setImageURI(Uri.parse(url));
                    item.imagesGridLayout.addView(drawView);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        // TODO multi item type
        if(position==0){
            return VIEW_TITLE;
        }else if(mReplyList.get(position-1)!=null){
            return VIEW_ITEM;
        }else{
            return VIEW_PROGRESS;
        }
    }

    @Override
    public int getItemCount() {
        return 1+(mReplyList==null?0:mReplyList.size());
    }








    class PostViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView avatarDraw;
        TextView tvName;
        TextView tvUniversity;
        TextView tvTime;
        TextView tvTitle;
        TextView tvContent;
        GridLayout imagesGridLayout; // post images

        TextView tvLikeNumber; // show like number
        ImageView ivLike;
        // for like clickListener
        LinearLayout likeLayout;

        TextView tvCommit; // show commit number
        LinearLayout commitLayout; // for commit listener

        LinearLayout llLikePeoples; // liked people

        public PostViewHolder(View itemView) {
            super(itemView);
            avatarDraw = (SimpleDraweeView) itemView.findViewById(R.id.aty_post_title_avatar);
            tvName = (TextView) itemView.findViewById(R.id.aty_post_title_user);
            tvUniversity = (TextView) itemView.findViewById(R.id.aty_post_title_university);
            tvTime = (TextView) itemView.findViewById(R.id.aty_post_title_time);
            tvTitle = (TextView) itemView.findViewById(R.id.aty_post_title_title);
            tvContent = (TextView) itemView.findViewById(R.id.aty_post_title_content);
            imagesGridLayout = (GridLayout) itemView.findViewById(R.id.aty_post_title_image);
            imagesGridLayout.setNumInRow(3);
            tvLikeNumber = (TextView) itemView.findViewById(R.id.aty_post_title_like_number);
            ivLike = (ImageView) itemView.findViewById(R.id.aty_post_title_like_image);
            tvCommit = (TextView) itemView.findViewById(R.id.aty_post_title_reply_number);
            commitLayout = (LinearLayout) itemView.findViewById(R.id.aty_post_title_reply_count);
            likeLayout = (LinearLayout) itemView.findViewById(R.id.aty_post_title_like_count);
            llLikePeoples = (LinearLayout) itemView.findViewById(R.id.aty_post_title_like_people);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView avatarDraw;
        TextView tvName;
        TextView tvUniversity;
        TextView tvTime;
        TextView tvContent;
        LinearLayout llReplyList;
        GridLayout imagesGridLayout;
        TextView tvLike;
        TextView tvCommit;
        LinearLayout llLike;
        LinearLayout llCommit;
        ImageView ivLike;

        public ItemViewHolder(View itemView) {
            super(itemView);
            avatarDraw = (SimpleDraweeView) itemView.findViewById(R.id.aty_post_reply_avatar);
            tvName = (TextView) itemView.findViewById(R.id.aty_post_reply_name);
            tvUniversity = (TextView) itemView.findViewById(R.id.aty_post_reply_university);
            tvTime = (TextView) itemView.findViewById(R.id.aty_post_reply_time);
            tvContent = (TextView) itemView.findViewById(R.id.aty_post_reply_content);
            llReplyList = (LinearLayout) itemView.findViewById(R.id.aty_post_reply_reply_list);
            imagesGridLayout = (GridLayout) itemView.findViewById(R.id.aty_post_reply_images);
            imagesGridLayout.setNumInRow(3);
            tvLike = (TextView) itemView.findViewById(R.id.aty_post_reply_like_number);
            llLike = (LinearLayout) itemView.findViewById(R.id.aty_post_reply_like_layout);
            ivLike = (ImageView) itemView.findViewById(R.id.aty_post_reply_like_image);
            tvCommit = (TextView) itemView.findViewById(R.id.aty_post_reply_comment_number);
            llCommit = (LinearLayout) itemView.findViewById(R.id.aty_post_reply_commit_layout);
        }
    }
    private static class ProgressViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
//            progressBar = (ProgressBar) v.findViewById(R.id.progress);
        }
    }

}