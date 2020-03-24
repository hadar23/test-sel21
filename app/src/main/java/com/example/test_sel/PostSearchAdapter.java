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
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.Course;
import com.example.test_sel.Classes.CoursePerUser;
import com.example.test_sel.Classes.MyFireBase;
import com.example.test_sel.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostSearchAdapter extends RecyclerView.Adapter<PostSearchAdapter.ViewHolder> {

    private List<CardInfo> mData;
    private List<User> mDatausers;
    private LayoutInflater layoutInflater;
    private ItemClickListener mClickListener;
    private String kind;
    private String userId, Mygrade;
    private Context context;
    private Intent intent;
    private int myMenu = 0;
    Boolean isSearchByUser = false;


    // data is passed into the constructor
    public PostSearchAdapter(Context context, List<CardInfo> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
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
        holder.txt_lastRow.setText(String.valueOf(pInfo.LastRow()));

        //if we came from search by user
        if (intent != null) {
            holder.img_more.setVisibility(View.INVISIBLE);
            isSearchByUser = true;
        }

        kind = String.valueOf(pInfo.kind());
        String imagePath = String.valueOf(pInfo.Image());
        if (kind.equals(context.getString(R.string.adaptersKindUser))) {
            holder.txt_lastRow.setVisibility(View.GONE);
            myMenu = R.menu.user_menu;
            try {
                //if image is recieves then set
                Picasso.get().load(imagePath).into(holder.userImage);
            } catch (Exception e) {
                //if ther is ane error while getting image
                Picasso.get().load(R.drawable.ic_person).into(holder.userImage);
            }
            holder.userPostId = ((User) mData.get(position)).getUserId();
            holder.userPhone = ((User) mData.get(position)).getPhone();
            userId = String.valueOf(((User) pInfo).getUserId());
        }

        if (kind.equals(context.getString(R.string.adaptersKindCourse))) {
            holder.img_more.setVisibility(View.INVISIBLE);
            holder.userImage.setImageResource(R.drawable.img_afeka_logo);
            holder.postId = ((Course) mData.get(position)).getCourseCode();
            holder.txt_lastRow.setVisibility(View.GONE);


        }
        if (kind.equals(context.getString(R.string.adaptersKindCoursePerUser))) {
            holder.txt_thirdRow.setText(context.getString(R.string.cardInfoMyGrade) + Mygrade);
            holder.txt_lastRow.setText(String.valueOf(pInfo.LastRow()));
            holder.img_more.setVisibility(View.INVISIBLE);
//            myMenu = R.menu.user_menu;
            // classifiedAd = (CoursePerUser) (mData).get(position);
            holder.postId = ((CoursePerUser) mData.get(position)).getCourseCode();
            holder.userPostId = ((CoursePerUser) mData.get(position)).getUserId();
            try {
                //if image is recieves then set
                Picasso.get().load(imagePath).into(holder.userImage);
            } catch (Exception e) {
                //if ther is ane error while getting image
                Picasso.get().load(R.drawable.ic_person).into(holder.userImage);
            }
        }

        if (kind.equals(context.getString(R.string.adaptersKindUserPerCourse))) {
            holder.txt_thirdRow.setText(context.getString(R.string.cardInfoMyGrade) + Mygrade);
            holder.txt_lastRow.setText(String.valueOf(pInfo.LastRow()));
            myMenu = R.menu.user_menu;
            try {
                //if image is recieves then set
                Picasso.get().load(imagePath).into(holder.userImage);
            } catch (Exception e) {
                //if ther is ane error while getting image
                Picasso.get().load(R.drawable.ic_person).into(holder.userImage);
            }
            holder.userPostId = ((CoursePerUser) mData.get(position)).getUserId();
            holder.userPhone = ((CoursePerUser) mData.get(position)).getUserPhone();
            userId = String.valueOf(((CoursePerUser) pInfo).getUserId());
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
                                holder.startActivityForSchedule();
                                return true;
                            case R.id.message:
                                //handle menu3 click
                                holder.sendMessage();
                                Toast.makeText(context, R.string.menuMessage, Toast.LENGTH_SHORT).show();
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
        androidx.cardview.widget.CardView txtlayout;
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
            itemView.setOnClickListener(this);
            txtlayout = itemView.findViewById(R.id.txtlayout);
            headlayout = itemView.findViewById(R.id.headlayout);
            context = itemView.getContext();
            this.postId = postId;
            this.userPostId = userPostId;
            // currentUserId=fA


        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, kind, Toast.LENGTH_SHORT).show();

            if (kind.equals(context.getString(R.string.adaptersKindUserPerCourse)) ||   (kind.equals(context.getString(R.string.adaptersKindCoursePerUser)))) {

                startActivityForSchedule();
            }

            if (kind.equals(context.getString(R.string.adaptersKindUser))) {
                Intent intent = new Intent(context, SearchMentoringActivity.class);
                intent.putExtra(context.getString(R.string.intentsExtrasKey), userPostId);
                intent.putExtra(context.getString(R.string.intentsExtrasType), context.getString(R.string.intentsExtrasComeFromUserFreg));
                context.startActivity(intent);
                ((Activity) context).finish();
            }

            if (kind.equals(context.getString(R.string.adaptersKindCourse))) {
                if (txt_thirdRow.getText().toString().trim().equals("0")) {
                    Toast.makeText(context, R.string.postSearchAdapterNoMentoring, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, SearchMentoringActivity.class);
                    intent.putExtra(context.getString(R.string.intentsExtrasKey), txt_secondRow.getText());
                    intent.putExtra(context.getString(R.string.intentsExtrasType), context.getString(R.string.intentsExtrasComeFromCourseFreg));
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }

            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void startActivityForProfile() {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(context.getString(R.string.intentsExtrasVisiitUserId), userPostId);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        public void startActivityForSchedule() {
            if (userPostId.equals(currentUserId)) {
                Toast.makeText(context, R.string.postSearchAdapterThisIsYou, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, ScheduleActivity.class);
            intent.putExtra(context.getString(R.string.intentsExtrasIsHost), false);
            intent.putExtra(context.getString(R.string.intentsExtrasHostUserId), userPostId);
            intent.putExtra(context.getString(R.string.intentsExtrasCourseCode), txt_secondRow.getText());
            intent.putExtra(context.getString(R.string.intentsExtrasCourseName), txt_firstRow.getText());
            context.startActivity(intent);
            //  ((Activity) context).finish();
        }

        public void startActivityForAddMentoring(Boolean isEdit) {
            Intent intent = new Intent(context, AddMentoringActivity.class);
            intent.putExtra(context.getString(R.string.intentsExtrasCodeC), txt_secondRow.getText());
            intent.putExtra(context.getString(R.string.intentsExtrasNameC), txt_firstRow.getText());
            if (isEdit) {
                intent.putExtra(context.getString(R.string.intentsExtrasGradeC), Mygrade);
                intent.putExtra(context.getString(R.string.intentsExtrasLevelC), txt_lastRow.getText());
                intent.putExtra(context.getString(R.string.intentsExtrasYesEdit), true);
            }
            context.startActivity(intent);
            ((Activity) context).finish();
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
            DatabaseReference refUser = FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.globalKeysUsers)).child(currentUserId);
            MyFireBase.addToCounter(refUser, context.getString(R.string.globalKeysCoursesCounter), -1);
            refUser.child(context.getString(R.string.globalKeysCoursesInUser)).child(s).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
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