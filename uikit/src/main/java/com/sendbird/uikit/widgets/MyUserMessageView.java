package com.sendbird.uikit.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.uikit.R;
import com.sendbird.uikit.consts.MessageGroupType;
import com.sendbird.uikit.databinding.SbViewMyUserMessageComponentBinding;
import com.sendbird.uikit.utils.DateUtils;
import com.sendbird.uikit.utils.DrawableUtils;
import com.sendbird.uikit.utils.ViewUtils;

public class MyUserMessageView extends GroupChannelMessageView {
    private SbViewMyUserMessageComponentBinding binding;
    private int editedAppearance;

    @Override
    public SbViewMyUserMessageComponentBinding getBinding() {
        return binding;
    }

    public MyUserMessageView(Context context) {
        this(context, null);
    }

    public MyUserMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.sb_message_user_style);
    }

    public MyUserMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageView_User, defStyle, 0);
        try {
            this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.sb_view_my_user_message_component, this, true);
            int timeAppearance = a.getResourceId(R.styleable.MessageView_User_sb_message_time_text_appearance, R.style.SendbirdCaption4OnLight03);
            int messageAppearance = a.getResourceId(R.styleable.MessageView_User_sb_message_me_text_appearance, R.style.SendbirdBody3OnDark01);
            int messageBackground = a.getResourceId(R.styleable.MessageView_User_sb_message_me_background, R.drawable.sb_shape_chat_bubble);
            int messageBackgroundTint = a.getResourceId(R.styleable.MessageView_User_sb_message_me_background_tint, R.color.sb_message_me_tint_light);
            int emojiReactionListBackground = a.getResourceId(R.styleable.MessageView_User_sb_message_emoji_reaction_list_background, R.drawable.sb_shape_chat_bubble_reactions_light);
            int ogtagBackground = a.getResourceId(R.styleable.MessageView_User_sb_message_me_ogtag_background, R.drawable.sb_message_og_background);
            int ogtagBackgroundTint = a.getResourceId(R.styleable.MessageView_User_sb_message_me_ogtag_background_tint, R.color.sb_message_other_tint_light);
            int linkTextColor = a.getResourceId(R.styleable.MessageView_User_sb_message_me_link_text_color, R.color.ondark_01);
            int clickedLinkBackgroundColor = a.getResourceId(R.styleable.MessageView_User_sb_message_me_clicked_link_background_color, R.color.primary_400);
            editedAppearance = a.getResourceId(R.styleable.MessageView_User_sb_message_my_edited_mark_text_appearance, R.style.SendbirdBody3OnDark02);
            this.highlightBackgroundColor = a.getResourceId(R.styleable.MessageView_User_sb_message_highlight_background_color, R.color.highlight);
            this.highlightForegroundColor = a.getResourceId(R.styleable.MessageView_User_sb_message_highlight_foreground_color, R.color.background_600);

            binding.tvMessage.setTextAppearance(context, messageAppearance);
            binding.tvMessage.setLinkTextColor(context.getResources().getColor(linkTextColor));
            binding.tvSentAt.setTextAppearance(context, timeAppearance);
            binding.contentPanel.setBackground(DrawableUtils.setTintList(getContext(), messageBackground, messageBackgroundTint));
            binding.emojiReactionListBackground.setBackgroundResource(emojiReactionListBackground);
            binding.ogtagBackground.setBackground(DrawableUtils.setTintList(getContext(), ogtagBackground, ogtagBackgroundTint));
            binding.ovOgtag.setBackground(DrawableUtils.setTintList(getContext(), ogtagBackground, ogtagBackgroundTint));

            binding.tvMessage.setOnClickListener(v -> binding.contentPanel.performClick());
            binding.tvMessage.setOnLongClickListener(v -> binding.contentPanel.performLongClick());
            binding.tvMessage.setOnLinkLongClickListener((v, link) -> binding.contentPanel.performLongClick());
            binding.tvMessage.setClickedLinkBackgroundColor(context.getResources().getColor(clickedLinkBackgroundColor));
            binding.ovOgtag.setOnLongClickListener(v -> binding.contentPanel.performLongClick());
        } finally {
            a.recycle();
        }
    }

    @Override
    public View getLayout() {
        return binding.getRoot();
    }

    @Override
    public void drawMessage(GroupChannel channel, BaseMessage message, MessageGroupType messageGroupType) {
        boolean sendingState = message.getSendingStatus() == BaseMessage.SendingStatus.SUCCEEDED;
        boolean hasOgTag = message.getOgMetaData() != null;
        boolean hasReaction = message.getReactions() != null && message.getReactions().size() > 0;

        binding.emojiReactionListBackground.setVisibility(hasReaction ? View.VISIBLE : View.GONE);
        binding.rvEmojiReactionList.setVisibility(hasReaction ? View.VISIBLE : View.GONE);
        binding.ogtagBackground.setVisibility(hasOgTag ? View.VISIBLE : View.GONE);
        binding.ovOgtag.setVisibility(hasOgTag ? View.VISIBLE : View.GONE);
        binding.tvSentAt.setVisibility((sendingState && (messageGroupType == MessageGroupType.GROUPING_TYPE_TAIL || messageGroupType == MessageGroupType.GROUPING_TYPE_SINGLE)) ? View.VISIBLE : View.GONE);
        binding.tvSentAt.setText(DateUtils.formatTime(getContext(), message.getCreatedAt()));
        binding.ivStatus.drawStatus(message, channel);

        ViewUtils.drawTextMessage(binding.tvMessage, message, editedAppearance, highlightMessageInfo, highlightBackgroundColor, highlightForegroundColor);
        ViewUtils.drawOgtag(binding.ovOgtag, message.getOgMetaData());
        ViewUtils.drawReactionEnabled(binding.rvEmojiReactionList, channel);

        int paddingTop = getResources().getDimensionPixelSize((messageGroupType == MessageGroupType.GROUPING_TYPE_TAIL || messageGroupType == MessageGroupType.GROUPING_TYPE_BODY) ? R.dimen.sb_size_1 : R.dimen.sb_size_8);
        int paddingBottom = getResources().getDimensionPixelSize((messageGroupType == MessageGroupType.GROUPING_TYPE_HEAD || messageGroupType == MessageGroupType.GROUPING_TYPE_BODY) ? R.dimen.sb_size_1 : R.dimen.sb_size_8);
        binding.root.setPadding(binding.root.getPaddingLeft(), paddingTop, binding.root.getPaddingRight(), paddingBottom);

        ViewUtils.drawQuotedMessage(binding.quoteReplyPanel, message);
    }
}
