package robotsmom.growow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by rettpop on 16-05-04.
 */
public class FarmGridView extends TextureView implements View.OnTouchListener
{
    private static final String LOG_TAG = "FarmGridView";

    private ArrayList<FarmCell> mCells;
    private float mFactorW, mFactorH;
    private float mFieldW, mFieldH;

    public void setFieldSize(float width, float height)
    {
        this.mFieldW = width;
        this.mFieldH = height;
    }

    /**
     * Instantiates a new Farm grid view.
     *
     * @param context main activity context to draw some stuff
     * @param cells   cells of the Bed to draw and tap them
     * @param factorW scaling factor by width. To translate real measurments to screen
     * @param factorH scaling factor by height. To translate real measurments to screen
     */
    public FarmGridView(Context context, ArrayList<FarmCell> cells, float factorW, float factorH)
    {
        super(context);
        mFactorW = factorW;
        mFactorH = factorH;
        mCells = cells;
    }

    public void selectCell(FarmCell cell)
    {
        for (FarmCell oneCell:mCells) {
            if(cell.getId() == oneCell.getId())
            {
                oneCell.setSelected( !oneCell.isSelected() );
                drawCells(mCells);
                break;
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if( MotionEvent.ACTION_DOWN != event.getAction() ) {
            return false;
        }
        Log.d(LOG_TAG, "Got touch in (" + event.getX() + ", " + event.getY() + ")");
        Log.d(LOG_TAG, "view is: " + view.toString());
        Log.d(LOG_TAG, "view.Left(" + view.getLeft() + "), Right(" + view.getRight() + ")" + ", Top(" + view.getTop() + ")");
        Log.d(LOG_TAG, "this.Left(" + this.getLeft() + "), Right(" + this.getRight() + ")" + ", Top(" + this.getTop() + ")");
        int[] gridLoc = new int[2];
        int[] viewLoc = new int[2];
        this.getLocationOnScreen(gridLoc);
        view.getLocationOnScreen(viewLoc);
        Log.d(LOG_TAG, "this.locationOnScreen(" + gridLoc[0] + ", " + gridLoc[1] + ")");

        PointF point = new PointF((event.getX() - gridLoc[0] + viewLoc[0]) / mFactorW, (event.getY() - gridLoc[1] + viewLoc[1]) / mFactorH);

        Log.d(LOG_TAG, "point.Left(" + point.x + ", " + point.y + ")");
        // find touched cell
        for (FarmCell oneCell: mCells)
        {
            if( oneCell.containsPoint(point) )
            {
                oneCell.setSelected( !oneCell.isSelected() );
            }
            else {
                // clear current selection
                oneCell.setSelected(false);
            }
        }

        drawCells(mCells);

        return true;
    }


    /**
     * Draw cells.
     *
     * @param cells the cells
     */
    public void drawCells(ArrayList<FarmCell> cells)
    {
        final int kActiveCellBorderColor = Color.RED;
        final float kActiveCellBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        final int kInactiveCellBorderColor = Color.GREEN;
        final float kInactiveCellBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        final int kActiveCellColorAlpha = 255;
        final int kInactiveCellColorAlpha = 150;
        final int kInactiveCellTitleFontSize = 12;
        final int kActiveCellTitleFontSize = kInactiveCellTitleFontSize;

        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, kInactiveCellTitleFontSize, getResources().getDisplayMetrics()));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        Rect textRect = new Rect();

        mFactorW = getWidth() / mFieldW;
        mFactorH = getHeight() / mFieldH;

        final Canvas canvas = this.lockCanvas();
        try
        {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            Path path = new Path();

            // first draw not celected cells. Then will draw selected ones.
            // overzhopnoe solution, but for MVP will prokatit
            // Need it to prevent overlapping selected cells by not selected.
            for (FarmCell oneCell:cells)
            {
                if( oneCell.isSelected() ) {
                    continue;
                }
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth( kInactiveCellBorderWidth );
                paint.setColor( kInactiveCellBorderColor );
                paint.setAlpha(kInactiveCellColorAlpha);

                path.reset();
                PointF[] cellPoints = oneCell.points();

                // move cursor to the top left point
                PointF ppt = cellPoints[0]; // projected point
                path.moveTo(ppt.x * mFactorW, ppt.y * mFactorH);

                //drawing lined in the counterclock direction to avoid additinal line from the last to the first point
                for (int idx = cellPoints.length - 1; idx >= 0; idx--) {
                    ppt = cellPoints[idx];
                    path.lineTo(ppt.x * mFactorW, ppt.y * mFactorH);
                }
                canvas.drawPath(path, paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.description();
                paint.getTextBounds(oneCell.description(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.description(),
                                (oneCell.left() + oneCell.width()/2) * mFactorW,
                                (oneCell.bottom() - textRect.height()) * mFactorH,
                                paint
                );
            }

            // draw selected cells
            // At the moment it is not assumed to be more then 1, but hz how it will be in future
            for (FarmCell oneCell:cells)
            {
                if( !oneCell.isSelected() ) {
                    continue;
                }
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth( kActiveCellBorderWidth );
                paint.setColor( kActiveCellBorderColor );
                paint.setAlpha(kActiveCellColorAlpha);

                path.reset();
                PointF[] cellPoints = oneCell.points();
                // move cursor to the top left point
                PointF ppt = cellPoints[0]; // projected point
                path.moveTo(ppt.x * mFactorW, ppt.y * mFactorH);

                //drawing lined in the counterclock direction to avoid additinal line from the last to the first point
                for (int idx = cellPoints.length - 1; idx >= 0; idx--) {
                    ppt = cellPoints[idx];
                    path.lineTo(ppt.x * mFactorW, ppt.y * mFactorH);
                }

                canvas.drawPath(path, paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.description();
                paint.getTextBounds(oneCell.description(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.description(),
                        (oneCell.left() + oneCell.width()/2) * mFactorW,
                        (oneCell.bottom() - textRect.height()) * mFactorH,
                        paint);
            }
        }
        finally {
            this.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Sets cells.
     *
     * @param cells the cells
     */
    public void setCells(ArrayList<FarmCell> cells) {
        this.mCells = cells;
    }

}
