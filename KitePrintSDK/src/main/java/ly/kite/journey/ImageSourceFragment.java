/*****************************************************
 *
 * ImageSourceFragment.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.journey;


///// Import(s) /////

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.product.Asset;
import ly.kite.product.Product;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class ImageSourceFragment extends AProductCreationFragment implements AdapterView.OnItemClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                                   = "ImageSourceFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private GridView  mImageSourceGridView;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ImageSourceFragment newInstance( Product product )
    {
    ImageSourceFragment fragment = new ImageSourceFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AEditImageFragment Method(s) //////////

  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_image_source, container, false );

    mImageSourceGridView = (GridView)view.findViewById( R.id.image_source_grid_view );


    // Determine the image sources

    ArrayList<ImageSource> imageSourceList = new ArrayList<>();

    imageSourceList.add( ImageSource.DEVICE );

    // Add the Instagram image source only if the SDK user has enabled it by providing a client id & redirect URI
    if ( KiteSDK.getInstance( getActivity() ).haveInstagramCredentials() ) imageSourceList.add( ImageSource.INSTAGRAM );


    // Set up the image source grid

    ImageSourceAdaptor imageSourceAdaptor = new ImageSourceAdaptor( mKiteActivity, R.layout.grid_item_image_source_vertical, imageSourceList );

    mImageSourceGridView.setAdapter( imageSourceAdaptor );

    mImageSourceGridView.setOnItemClickListener( this );


    return ( view );
    }


  /*****************************************************
   *
   * Called with the result of an activity.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent returnedIntent )
    {
    super.onActivityResult( requestCode, resultCode, returnedIntent );


    // Get assets for any images returned

    List<Asset> newAssetList = ImageSource.getAssetsFromResult( requestCode, resultCode, returnedIntent );

    if ( newAssetList != null && newAssetList.size() > 0 )
      {
      // Add the assets to the assets / quantity list held by the activity

      for ( Asset newAsset : newAssetList )
        {
        mAssetsAndQuantityArrayList.add( new AssetsAndQuantity( newAsset ) );
        }


      // If we got at least one asset - call back to the activity. Otherwise we say on this screen
      // unless the user pressed back.
      if ( mKiteActivity instanceof ICallback )
        {
        ( (ICallback)mKiteActivity ).isOnAssetsAdded();
        }
      }

    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();

    mKiteActivity.setTitle( R.string.title_image_source );
    }


  ////////// AdapterView.OnItemClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when an item is clicked.
   *
   *****************************************************/
  @Override
  public void onItemClick( AdapterView<?> parent, View view, int position, long id )
    {
    if ( parent == mImageSourceGridView )
      {
      ImageSource imageSource = (ImageSource)mImageSourceGridView.getItemAtPosition( position );

      imageSource.onPick( this, mProduct.getUserJourneyType().usesSingleImage() );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void isOnAssetsAdded();
    }


  }

