/*****************************************************
 *
 * LabelledImageView.java
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.widget;


///// Import(s) /////

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ly.kite.util.IImageConsumer;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a widget that displays a image potentially
 * with a label. It allows images to be supplied at a later
 * stage, fading them in where appropriate.
 *
 *****************************************************/
public class LabelledImageView extends AFixableImageFrame implements IImageConsumer, Animation.AnimationListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                           = "LabelledImageView";

  private static final long    FADE_IN_ANIMATION_DURATION_MILLIS = 300L;
  private static final float   ALPHA_TRANSPARENT                 = 0.0f;
  private static final float   ALPHA_OPAQUE                      = 1.0f;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ImageView     mEmptyFrameImageView;
  private OverlayLabel  mOverlayLabel;
  private ProgressBar   mProgressBar;

  private Object        mKey;

  private Animation     mFadeInAnimation;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public LabelledImageView( Context context )
    {
    super( context );
    }

  public LabelledImageView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public LabelledImageView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );
    }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public LabelledImageView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
    super( context, attrs, defStyleAttr, defStyleRes );
    }


  ////////// AFixableImageFrame Method(s) //////////

  /*****************************************************
   *
   * Returns the content view.
   *
   *****************************************************/
  @Override
  protected View onCreateView( Context context )
    {
    // Inflate the layout and attach it to this view

    LayoutInflater layoutInflater = LayoutInflater.from( context );

    View view = layoutInflater.inflate( R.layout.labelled_image_view, this, true );


    // Save references to the child views
    mEmptyFrameImageView = (ImageView)view.findViewById( R.id.empty_frame_image_view );
    mOverlayLabel        = (OverlayLabel)view.findViewById( R.id.overlay_label );
    mProgressBar         = (ProgressBar)view.findViewById( R.id.progress_bar );


    // Set up the overlay label

    Resources resources = context.getResources();

    mOverlayLabel.setCornerRadius( resources.getDimension( R.dimen.labelled_image_label_corner_radius ) );
    mOverlayLabel.setBackgroundShadow(
            resources.getColor( R.color.labelled_image_label_shadow ),
            resources.getDimension( R.dimen.labelled_image_label_shadow_blur_radius ),
            resources.getDimension( R.dimen.labelled_image_label_shadow_y_offset ) );


    return ( view );
    }


  ////////// RemoteImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Indicates that the image is being downloaded.
   *
   *****************************************************/
  @Override
  public void onImageDownloading( Object key )
    {
    mEmptyFrameImageView.setVisibility( View.VISIBLE );
    mImageView.setVisibility( View.INVISIBLE );
    mProgressBar.setVisibility( View.VISIBLE );
    }


  /*****************************************************
   *
   * Sets the image after it has been loaded.
   *
   *****************************************************/
  @Override
  public void onImageAvailable( Object key, Bitmap bitmap )
    {
    // Make sure the image is the one we were expecting.
    synchronized ( this )
      {
      if ( key == mKey )
        {
        setImage( bitmap );

        fadeImageIn();
        }
      }
    }


  ////////// Animation.AnimationListener Method(s) //////////

  /*****************************************************
   *
   * Called when an animation starts.
   *
   *****************************************************/
  @Override
  public void onAnimationStart( Animation animation )
    {
    // Ignore
    }


  /*****************************************************
   *
   * Called when an animation completes.
   *
   *****************************************************/
  @Override
  public void onAnimationEnd( Animation animation )
    {
    // Make the empty frame invisible only when the photo image fade in has finished
    if ( animation == mFadeInAnimation )
      {
      mEmptyFrameImageView.setVisibility( View.GONE );
      }
    }


  /*****************************************************
   *
   * Called when an animation repeats.
   *
   *****************************************************/
  @Override
  public void onAnimationRepeat( Animation animation )
    {
    // Ignore
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the image.
   *
   *****************************************************/
  public void setImage( Bitmap bitmap )
    {
    mImageView.setImageBitmap( bitmap );

    mImageView.setVisibility( View.VISIBLE );
    mProgressBar.setVisibility( View.INVISIBLE );
    }


  /*****************************************************
   *
   * Sets the label text.
   *
   *****************************************************/
  public void setLabel( String label )
    {
    mOverlayLabel.setText( label );

    mOverlayLabel.setVisibility( label != null ? View.VISIBLE : View.INVISIBLE );
    }


  /*****************************************************
   *
   * Sets the label text and colour.
   *
   *****************************************************/
  public void setLabel( String label, int colour )
    {
    setLabel( label );

    mOverlayLabel.setBackgroundColor( colour );
    }


  /*****************************************************
   *
   * Clears the image, and sets an expected image key.
   *
   *****************************************************/
  public void clearForNewImage( Object expectedKey )
    {
    setExpectedKey( expectedKey );

    mImageView.setImageBitmap( null );
    }


  /*****************************************************
   *
   * Sets an expected image key.
   *
   *****************************************************/
  public void setExpectedKey( Object key )
    {
    mKey = key;
    }


  /*****************************************************
   *
   * Starts a fade-in animation on the image.
   *
   *****************************************************/
  private void fadeImageIn()
    {
    mFadeInAnimation = new AlphaAnimation( ALPHA_TRANSPARENT, ALPHA_OPAQUE );
    mFadeInAnimation.setDuration( FADE_IN_ANIMATION_DURATION_MILLIS );

    // We make the empty frame invisible only after the fade in animation has finished.
    mFadeInAnimation.setAnimationListener( this );

    mImageView.startAnimation( mFadeInAnimation );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

