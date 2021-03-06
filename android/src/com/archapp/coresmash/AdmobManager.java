package com.archapp.coresmash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.archapp.coresmash.platform.AdManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/* Important information to avoid confusion:
 *      First of all this class is tied with the Android platform. With that in mind it is really
 *  important to understand that there will be _TWO_ threads that will be working with this class.
 *  It is the Android's Main Thread or else the 'UI Thread' and then there is the Games' Thread or the LibGdx Thread.
 *  All the Android UI stuff (including ALL the ad types) are in the Android's thread and all the triggers
 *  for the ads to be shown are in the Game's thread. The Game's thread _CANNOT_ directly alter the Android UI
 *  that was initialized and is managed by the Android thread even though it *does* have access to them through this
 *  class. In order for the two threads to communicate, they use a Handler that is initialized in the Android's thread
 *  which works similar to the Windows loop. The Game's thread has to send messages or runnables that get queued
 *  in the Android thread and get executed on that thread.
 */

public final class AdmobManager implements AdManager {
    private static final int ADSHOW = 1;
    private static final int ADHIDE = 0;

    private AdView adView;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> task;
    private Runnable dismissDialog;
    private ProgressDialog dialog;
    private RewardedVideoAd rewardedVideoAd;
    private AdRewardListener rewardListener;
    private Handler handler;

    public AdmobManager() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ADSHOW:
                        adView.setVisibility(View.VISIBLE);
                        break;
                    case ADHIDE:
                        adView.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }

    public void init(final AndroidApplication context, final RelativeLayout layout) {
        MobileAds.initialize(context, "ca-app-pub-9627776817799661~8252497910");
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);

        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface anInterface) {
                rewardListener.canceled();
                task.cancel(true);
            }
        });

        executorService = Executors.newSingleThreadScheduledExecutor();

        dismissDialog = new Runnable() {
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    rewardListener.canceled();
                    Toast.makeText(context, "Failed to load. Check your connection!", Toast.LENGTH_LONG).show();
                }
            };

            @Override
            public void run() {
                handler.post(action);
            }
        };

        adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.GONE);
                adView.requestLayout();
            }
        });

//        adView.loadAd(new AdRequest.Builder().addTestDevice("EA655C74DEA919ECE1BFC5F57C5C8708").build());

        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);// Shows Ads on Center Bottom
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layout.addView(adView, adParams);
    }

    @Override
    public void show() {
        handler.sendEmptyMessage(ADSHOW);
    }

    @Override
    public void showAdForReward(final AdRewardListener listener, final VideoAdRewardType type) {
        rewardListener = listener;
        handler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                if (rewardedVideoAd.isLoaded()) {
                    rewardedVideoAd.show();
                } else {
                    dialog.show();
                    String adID;
                    switch (type) {
                        case LOTTERY_COIN:
                            adID = "ca-app-pub-9627776817799661/8324650107";
                            break;
                        case EXTRA_LIFE:
                            adID = "ca-app-pub-9627776817799661/6318755052";
                            break;
                        case HEART:
                            adID = "ca-app-pub-9627776817799661/8314363009";
                            break;
                        default:
                            throw new RuntimeException("Not known ad type");
                    }

                    if (CoreSmash.DEV_MODE)
                        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().addTestDevice("6E51061518C2EC71A871160C702767EB").build());
                    else
                        rewardedVideoAd.loadAd(adID, new AdRequest.Builder().build());

                    rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoAdLoaded() {
                            if (dialog.isShowing()) {
                                task.cancel(true);
                                dialog.dismiss();
                                rewardedVideoAd.show();
                            }
                        }

                        @Override
                        public void onRewardedVideoAdOpened() {

                        }

                        @Override
                        public void onRewardedVideoStarted() {

                        }

                        @Override
                        public void onRewardedVideoAdClosed() {
                            listener.canceled();
                        }

                        @Override
                        public void onRewarded(RewardItem item) {
                            listener.reward(item.getType(), item.getAmount());
                        }

                        @Override
                        public void onRewardedVideoAdLeftApplication() {
                            listener.canceled();
                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int i) {
                            task.cancel(true);
                            dismissDialog.run();
                        }

                        @Override
                        public void onRewardedVideoCompleted() {
                        }
                    });
                    task = executorService.schedule(dismissDialog, 30, TimeUnit.SECONDS);
                }
            }
        });
    }

    @Override
    public void toggle() {
        if (adView.isShown()) {
            handler.sendEmptyMessage(ADHIDE);
        } else {
            handler.sendEmptyMessage(ADSHOW);
        }
    }

    @Override
    public void hide() {
        handler.sendEmptyMessage(ADHIDE);
    }

}