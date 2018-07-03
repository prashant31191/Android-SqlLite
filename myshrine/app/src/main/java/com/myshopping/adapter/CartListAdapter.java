package com.myshopping.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myshopping.R;
import com.myshopping.application.App;
import com.myshopping.network.ImageRequester;
import com.myshopping.network.ProductEntry;

import java.util.List;

/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
public class CartListAdapter extends RecyclerView.Adapter<CartListViewHolder> {

    private List<ProductEntry> productList;
    private ImageRequester imageRequester;
    private Context mContext;

    public CartListAdapter(Context context, List<ProductEntry> productList) {
        this.productList = productList;
        this.mContext = context;
        imageRequester = ImageRequester.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.shr_staggered_product_card_first;
        if (viewType == 1) {
            layoutId = R.layout.shr_staggered_product_card_second;
        } else if (viewType == 2) {
            layoutId = R.layout.shr_staggered_product_card_third;
        }

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new CartListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, final int position) {
        if (productList != null && position < productList.size()) {
            ProductEntry product = productList.get(position);
            holder.productTitle.setText(product.title);
            holder.productPrice.setText(product.price);

            imageRequester.setImageFromUrl(holder.productImage, product.url);


            holder.ivAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    App.cartProductList.remove(position);
                    //productList.remove(position);
                    notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
