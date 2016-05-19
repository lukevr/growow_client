package robotsmom.growow;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

/**
 * Created by rettpop on 16-05-19.
 */
interface FarmSurfaceTextureListener extends TextureView.SurfaceTextureListener
{
    public void setResizeStream(boolean resizeStream);
}
