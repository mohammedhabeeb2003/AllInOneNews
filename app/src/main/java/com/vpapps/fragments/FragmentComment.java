package com.vpapps.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vpapps.AsyncTask.LoadCommentDelete;
import com.vpapps.AsyncTask.LoadCommentPost;
import com.vpapps.AsyncTask.LoadComments;
import com.vpapps.adapter.AdapterCommentEndless;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.ClickListener;
import com.vpapps.interfaces.CommentDeleteListener;
import com.vpapps.interfaces.CommentListener;
import com.vpapps.interfaces.PostCommentListener;
import com.vpapps.item.ItemComment;
import com.vpapps.item.ItemEventBus;
import com.vpapps.utils.Constant;
import com.vpapps.utils.EndlessRecyclerViewScrollListener;
import com.vpapps.utils.GlobalBus;
import com.vpapps.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentComment extends BottomSheetDialogFragment {

    private Methods methods;
    private RecyclerView rv_comment;
    private AdapterCommentEndless adapter;
    private ArrayList<ItemComment> arrayList;
    private ImageView button_send;
    private EditText editText_comment;
    private CircularProgressBar progressBar;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    private ProgressDialog progressDialog;

    private int page = 1;
    private Boolean isOver = false, isScroll = false;
    private BottomSheetBehavior mBehavior;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.layout_comment_dialog, null);

        methods = new Methods(getActivity());
        arrayList = new ArrayList<>();
        errr_msg = getString(R.string.no_comment);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));

        editText_comment = view.findViewById(R.id.editText_addcomment);
        rv_comment = view.findViewById(R.id.rv_comment);
        button_send = view.findViewById(R.id.imageView_postcomment);

        textView_empty = view.findViewById(R.id.textView_empty_msg);
        button_try = view.findViewById(R.id.button_empty_try);
        ll_empty = view.findViewById(R.id.ll_empty);
        progressBar = view.findViewById(R.id.progressBar_comment);

        editText_comment.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv_comment.setLayoutManager(llm);

        rv_comment.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            loadComments();
                        }
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadComments();
            }
        });

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.isLogged) {
                    methods.openLogin();
                } else if (editText_comment.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                } else {
                    if (methods.isNetworkAvailable()) {
                        loadPostComment();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.conn_net_post_comment), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loadComments();

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void loadComments() {
        if (methods.isNetworkAvailable()) {
            LoadComments loadComments = new LoadComments(new CommentListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        progressBar.setVisibility(View.VISIBLE);
                        rv_comment.setVisibility(View.GONE);
                        ll_empty.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onEnd(String success, ArrayList<ItemComment> arrayListComment) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (arrayListComment.size() == 0) {
                                isOver = true;
                                try {
                                    adapter.hideHeader();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                page = page + 1;
                                arrayList.addAll(arrayListComment);
                            }

                            errr_msg = getString(R.string.no_comment);
                        } else {
                            errr_msg = getString(R.string.err_server_no_conn);
                        }
                        setAdapter();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_COMMENTS, page, "", Constant.itemNewsCurrent.getId(), "", "", "", "", "", "", "", "", "", "", null));
            loadComments.execute();
        } else {
            isOver = true;
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    public void setAdapter() {
        if (!isScroll) {
            adapter = new AdapterCommentEndless(getActivity(), arrayList, new ClickListener() {
                @Override
                public void onClick(int pos) {
                    loadDeleteComment(pos);
                }
            });
            rv_comment.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadDeleteComment(final int pos) {
        LoadCommentDelete loadCommentDelete = new LoadCommentDelete(new CommentDeleteListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String isDeleted, String message, ItemComment itemComment) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    if (isDeleted.equals("1")) {
                        GlobalBus.getBus().postSticky(new ItemEventBus(getString(R.string.delete), arrayList.get(pos), pos));
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        arrayList.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        setEmpty();

                        if (pos < 5 && arrayList.size() >= 5) {
                            GlobalBus.getBus().postSticky(new ItemEventBus(getString(R.string.comment_old), arrayList.get(4), 0));
                        }
                    }
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_DELETE_COMMENTS, 0, "", Constant.itemNewsCurrent.getId(), "", "", arrayList.get(pos).getId(), "", "", "", "", "", "", "", null));
        loadCommentDelete.execute();
    }

    public void setEmpty() {
        if (arrayList.size() > 0) {
            rv_comment.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            rv_comment.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private void loadPostComment() {
        if (methods.isNetworkAvailable()) {
            LoadCommentPost loadCommentPost = new LoadCommentPost(new PostCommentListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String isCommentPosted, String message, ItemComment itemComment) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (isCommentPosted.equals("1")) {
                                arrayList.add(0, itemComment);
                                adapter.notifyDataSetChanged();
                                rv_comment.setVisibility(View.VISIBLE);
                                ll_empty.setVisibility(View.GONE);
                                editText_comment.setText("");
                                rv_comment.smoothScrollToPosition(0);

                                GlobalBus.getBus().postSticky(new ItemEventBus(getString(R.string.comments), itemComment, 0));
                            }
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_POST_COMMENTS, 0, "", Constant.itemNewsCurrent.getId(), editText_comment.getText().toString(), "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadCommentPost.execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }
}