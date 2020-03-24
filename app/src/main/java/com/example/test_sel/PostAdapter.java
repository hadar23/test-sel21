package com.example.test_sel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_sel.Callbacks.CallBack_IsExistReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.Course;
import com.example.test_sel.Classes.CoursePerUser;
import com.example.test_sel.Classes.Message;
import com.example.test_sel.Classes.MyFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<CardInfo> mData;
    private LayoutInflater layoutInflater;
    private ItemClickListener mClickListener;
    private String kind;
    private String userId, Mygrade, myuserid;
    private Context context;
    private Intent intent = null;
    private int myMenu = 0;
    String otheruser = null;
    private Boolean isVisit = false;


    // data is passed into the constructor
    public PostAdapter(Context context, List<CardInfo> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // data is passed into the constructor with intent
    PostAdapter(Context context, List<CardInfo> data, Intent intent) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.intent = intent; // use this intent
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_post, parent, false);
        return new ViewHolder(view, "", "");
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int itemPos = position;
        final CardInfo pInfo = mData.get(position);

        Mygrade = String.valueOf(pInfo.ThirdRow());
        holder.txt_titleUser.setText(String.valueOf(pInfo.Title()));
        holder.txt_firstRow.setText(pInfo.FirstRow());
        holder.txt_secondRow.setText(String.valueOf(pInfo.SecondRow()));
        holder.txt_thirdRow.setText(String.valueOf(pInfo.ThirdRow()));

        kind = String.valueOf(pInfo.kind());
        String imagePath = String.valueOf(pInfo.Image());


        if (kind.equals(context.getString(R.string.adaptersKindCourse))) {
            holder.img_more.setVisibility(View.INVISIBLE);
            // holder.img_head.setVisibility(View.GONE);
            holder.userImage.setImageResource(R.drawable.img_afeka_logo);
            holder.postId = ((Course) mData.get(position)).getCourseCode();
            holder.txt_lastRow.setVisibility(View.GONE);
        }
        if (kind.equals(context.getString(R.string.adaptersKindCoursePerUser))) {
            holder.headlayout.getBackground().setTint((ContextCompat.getColor(context, R.color.stblue)));
            holder.txtlayout.getBackground().setTint((ContextCompat.getColor(context, R.color.liblue)));

            otheruser = String.valueOf(((CoursePerUser) pInfo).getUserId());
            if (!holder.currentUserId.equals(otheruser))
                holder.img_more.setVisibility(View.INVISIBLE);
            holder.txt_thirdRow.setText(context.getString(R.string.cardInfoMyGrade) + Mygrade);
            holder.txt_lastRow.setText(String.valueOf(pInfo.LastRow()));
            myMenu = R.menu.course_per_user_menu;
        }
        if (kind.equals(context.getString(R.string.adaptersKindMessage))) {
            if (holder.currentUserId.equals(((Message) mData.get(position)).getHostUserId())) {
                holder.userPostId = ((Message) mData.get(position)).getGuestUserId();
            } else {
                holder.userPostId = ((Message) mData.get(position)).getHostUserId();
            }
            if (((Message) pInfo).getTypeOfMessage().equals(Message.CANCEL_MESSAGE)) {
                //red
                holder.headlayout.getBackground().setTint((ContextCompat.getColor(context, R.color.stred)));
                holder.txtlayout.getBackground().setTint((ContextCompat.getColor(context, R.color.lired)));
            } else {
                //green
                holder.headlayout.getBackground().setTint((ContextCompat.getColor(context, R.color.stgreen)));
                holder.txtlayout.getBackground().setTint((ContextCompat.getColor(context, R.color.ligreen)));
            }
            myMenu = R.menu.profilemenu;
            holder.txt_lastRow.setText(String.valueOf(pInfo.LastRow()));
            try {
                //if image is recieves then set
                Picasso.get().load(imagePath).into(holder.userImage);
            } catch (Exception e) {
                //if ther is ane error while getting image
                Picasso.get().load(R.drawable.ic_person).into(holder.userImage);
            }
        }

        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.img_more);
                //inflating menu from xml resource
                popup.inflate(myMenu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.profile:
                                //handle menu1 click
                                Toast.makeText(context, R.string.menuProfile, Toast.LENGTH_SHORT).show();
                                holder.startActivityForProfile();
                                return true;
                            case R.id.schedule:
                                //handle menu2 click

                                Toast.makeText(context, R.string.menuSchedule, Toast.LENGTH_SHORT).show();

                                return true;
                            case R.id.message:
                                //handle menu3 click
                                holder.sendMessage();
                                Toast.makeText(context, R.string.menuMessage, Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.delete:
                                //handle menu3 click
                                holder.deleteItem(((CoursePerUser) pInfo).getCourseCode(), itemPos);
//                                deleteClassifiedAd(classifiedAd.getAdId(), itemPos);
                                Toast.makeText(context, R.string.menuDelete, Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.edit:
                                //handle menu3 click

                                holder.startActivityForAddMentoring(true);
                                Toast.makeText(context, R.string.menuEdit, Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }

        });


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_titleUser;
        TextView txt_firstRow;
        TextView txt_secondRow;
        TextView txt_thirdRow;
        TextView txt_lastRow;
        ImageView img_more, userImage, img_head;
        Context context;
        CardView txtlayout;
        LinearLayout headlayout;

        String postId, userPostId, userPhone, currentUserId;

        ViewHolder(View itemView, String postId, String userPostId) {
            super(itemView);

            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            img_head = itemView.findViewById(R.id.image_mentoring);
            txt_titleUser = itemView.findViewById(R.id.txt_titleUser);
            txt_firstRow = itemView.findViewById(R.id.txt_firstRow);
            txt_secondRow = itemView.findViewById(R.id.txt_secondRow);
            txt_thirdRow = itemView.findViewById(R.id.txt_thirdRow);
            txt_lastRow = itemView.findViewById(R.id.txt_lastRow);
            img_more = itemView.findViewById(R.id.btn_more);
            userImage = itemView.findViewById(R.id.userImage);
            txtlayout = itemView.findViewById(R.id.txtlayout);
            headlayout = itemView.findViewById(R.id.headlayout);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            this.postId = postId;
            this.userPostId = userPostId;

        }

        @Override
        public void onClick(View view) {

            if (kind.equals(context.getString(R.string.adaptersKindCoursePerUser)))
                if (currentUserId.equals(otheruser))
                    startActivityForAddMentoring(true);

            if (kind.equals(context.getString(R.string.adaptersKindCourse)))
                startActivityForAddMentoring(false);

            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void startActivityForProfile() {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(context.getString(R.string.intentsExtrasVisiitUserId), userPostId);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        public void startActivityForAddMentoring(final Boolean isEdit) {
            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.globalKeysUsers)).child(currentUserId);
            MyFireBase.checkValueExist(refUser.child(context.getString(R.string.globalKeysCoursesInUser)), txt_secondRow.getText().toString(),
                    new CallBack_IsExistReady() {
                        @Override
                        public void isExistReady(boolean isExist) {
                            if (isExist && !isEdit) {
                                Toast.makeText(context, R.string.postAdapterAlreadyHaveCourse, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(context, AddMentoringActivity.class);
                            intent.putExtra(context.getString(R.string.intentsExtrasCodeC), txt_secondRow.getText());
                            intent.putExtra(context.getString(R.string.intentsExtrasNameC), txt_firstRow.getText());
                            if (isExist) {
                                intent.putExtra(context.getString(R.string.intentsExtrasGradeC), Mygrade);
                                intent.putExtra(context.getString(R.string.intentsExtrasLevelC), txt_lastRow.getText());
                                intent.putExtra(context.getString(R.string.intentsExtrasYesEdit), true);
                            }
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }

                        @Override
                        public void error() {

                        }
                    });
        }

        //send Message to user
        private void sendMessage() {
            Uri uri = Uri.parse("smsto:" + userPhone);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            context.startActivity(Intent.createChooser(i, ""));
        }


        //delete item from list
        public void deleteItem(String s, final int itemPos) {
            //selte from users
            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.globalKeysUsers)).child(currentUserId);
            MyFireBase.addToCounter(refUser, context.getString(R.string.globalKeysCoursesCounter), -1);
            refUser.child(context.getString(R.string.globalKeysCoursesInUser)).child(s).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

//                                mData.remove(itemPos);
                                notifyItemRemoved(itemPos);
                                notifyItemRangeChanged(itemPos, getItemCount());
                            } else {

                            }
                        }
                    });

            //selte from coursers
            DatabaseReference refcourses = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.globalKeysCourses)).child(s);
            MyFireBase.addToCounter(refcourses, context.getString(R.string.globalKeysUsersCounter), -1);
            refcourses.child(context.getString(R.string.globalKeysUsersList)).child(currentUserId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mData.remove(itemPos);
                                notifyItemRemoved(itemPos);
                                notifyItemRangeChanged(itemPos, getItemCount());
                            } else {
                            }
                        }
                    });
        }

    }

    // convenience method for getting data at click position
    CardInfo getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
