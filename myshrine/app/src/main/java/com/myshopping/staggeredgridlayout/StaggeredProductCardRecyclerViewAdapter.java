package com.myshopping.staggeredgridlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myshopping.NavigationHost;
import com.myshopping.ProductCartListFragment;
import com.myshopping.R;
import com.myshopping.application.App;
import com.myshopping.network.ImageRequester;
import com.myshopping.network.ProductEntry;

import java.util.List;

/**
 * Adapter used to show an asymmetric grid of products, with 2 items in the first column, and 1
 * item in the second column, and so on.
 */
public class StaggeredProductCardRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredProductCardViewHolder> {

    private List<ProductEntry> productList;
    private ImageRequester imageRequester;
    private Context mContext;

    public StaggeredProductCardRecyclerViewAdapter(Context context, List<ProductEntry> productList) {
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
    public StaggeredProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.shr_staggered_product_card_first;
        if (viewType == 1) {
            layoutId = R.layout.shr_staggered_product_card_second;
        } else if (viewType == 2) {
            layoutId = R.layout.shr_staggered_product_card_third;
        }

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new StaggeredProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull StaggeredProductCardViewHolder holder, final int position) {
        if (productList != null && position < productList.size()) {
            final ProductEntry product = productList.get(position);
            holder.productTitle.setText(product.title);
            holder.productPrice.setText(product.price);
            if (product.is_added_cart.equalsIgnoreCase("1")) {
                holder.ivAddToCart.setSelected(true);
            } else {
                holder.ivAddToCart.setSelected(false);
            }

            imageRequester.setImageFromUrl(holder.productImage, product.url);


            holder.ivAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (productList.get(position).is_added_cart.equalsIgnoreCase("0")) {
                        productList.get(position).is_added_cart = "1";
                        App.cartProductList.add(productList.get(position));

                        notifyDataSetChanged();

                        App.showSnackBar(view,"Product added to cart.");
                        //((NavigationHost) mContext).navigateTo(new ProductCartListFragment(), true); // Navigate to the next Fragment
                    } else {
                        productList.get(position).is_added_cart = "0";
                        if (App.cartProductList.contains(productList.get(position))) {
                            App.cartProductList.remove(productList.get(position));
                        }
                        App.showSnackBar(view,"Product removed from cart.");

                        notifyDataSetChanged();
                    }


                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
