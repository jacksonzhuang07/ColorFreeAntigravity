package com.example.colorfree;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class BillingManager {
    private static final String TAG = "BillingManager";
    private static final String PRODUCT_ID = "remove_ads_weekly";
    
    private BillingClient billingClient;
    private final Context context;
    private final SubscriptionStatusListener listener;

    public interface SubscriptionStatusListener {
        void onSubscriptionStatusChanged(boolean isSubscribed);
    }

    public BillingManager(Context context, SubscriptionStatusListener listener) {
        this.context = context;
        this.listener = listener;
        initBilling();
    }

    private void initBilling() {
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        
        startConnection();
    }

    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    checkSubscription();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request.
                // For simplicity, we just log it.
            }
        });
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
    };

    public void launchPurchaseFlow(Activity activity) {
        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
             if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                 ProductDetails productDetails = productDetailsList.get(0);
                 
                 // Needed for subscriptions
                 ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                    ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                            .build()
                    );

                 BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();
                    
                 billingClient.launchBillingFlow(activity, billingFlowParams);
             } else {
                 Log.e(TAG, "Product not found or error: " + billingResult.getDebugMessage());
             }
        });
    }

    private void checkSubscription() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
            (billingResult, purchases) -> {
                boolean subscribed = false;
                if (purchases != null) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getProducts().contains(PRODUCT_ID) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                            subscribed = true;
                            break;
                        }
                    }
                }
                listener.onSubscriptionStatusChanged(subscribed);
            }
        );
    }
    
    private void handlePurchases(List<Purchase> purchases) {
        boolean subscribed = false;
        for (Purchase purchase : purchases) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                // Acknowledge the purchase if it hasn't been acknowledged yet.
                /*
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchaseResult -> ...);
                }
                */
                // For MVP, just assuming verified
                if (purchase.getProducts().contains(PRODUCT_ID)) {
                    subscribed = true;
                }
            }
        }
        listener.onSubscriptionStatusChanged(subscribed);
    }
}
