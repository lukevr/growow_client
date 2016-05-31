package robotsmom.growow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
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

    private ArrayList<FarmCell> _cells;
    private float _factorW, _factorH;


    private float _fieldW, _fieldH;

    /**
     * Sets field w.
     *
     * @param fieldW the field w
     */
    //TODO: Take scale factor not there but in container view.
    public void setFieldW(float fieldW) {
        this._fieldW = fieldW;
    }

    /**
     * Sets field h.
     *
     * @param fieldH the field h
     */
    public void setFieldH(float fieldH) {
        this._fieldH = fieldH;
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
        _factorW = factorW;
        _factorH = factorH;
        _cells = cells;
    }

//    public FarmGridView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public FarmGridView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

    /**
     * Fill cells array list.
     *
     * @param cols the cols
     * @param rows the rows
     * @return the array list
     */
// fills cells array with fake cells. Just for test
    public ArrayList<FarmCell> fillCells(int cols, int rows)
    {
        String[] vegetables = {"Carrot", "Tomato", "Berry", "Cucumber", "Ukrop", "Patison", "Corn", "Pumpkin"};
        ArrayList<FarmCell> cells = new ArrayList<FarmCell>();
        float gridWidth = this.getWidth();
        float gridHeight = this.getHeight();

        float cellWidth = gridWidth / cols;
        float cellHeight = gridHeight / rows;

        for(int idr = 0; idr < rows; idr++)
        {
            for(int idc = 0; idc < cols; idc++)
            {
                FarmCell newCell = new FarmCell(idc * cellWidth, idr * cellHeight, cellWidth, cellHeight);

                // add random description
                int descrIdx = (int) (Math.random() * vegetables.length);
                newCell.setDescription(vegetables[descrIdx]);

                cells.add(newCell);
            }
        }

        return cells;
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

        //drawGrid(this, 5, 3, event.getX(), event.getY());
//        if( null == _cells ) {
//            _cells = fillCells(5, 4);
//        }

        PointF point = new PointF((event.getX() - gridLoc[0] + viewLoc[0]) / _factorW, (event.getY() - gridLoc[1] + viewLoc[1]) / _factorH);

        Log.d(LOG_TAG, "point.Left(" + point.x + ", " + point.y + ")");
        // find touched cell
        for (FarmCell oneCell:_cells)
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

        drawCells(_cells);

        return true;
    }


    /**
     * Draw cells.
     *
     * @param cells the cells
     */
    public void drawCells(ArrayList<FarmCell> cells)
    {
        // do these calculations here for a while
        Log.d(LOG_TAG, "Grid view size: " + this.getWidth() + "x" + this.getHeight());

        //=======================================================================================

        final int kActiveCellBorderColor = Color.RED;
        final float kActiveCellBorderWidth = 10.f;
        final int kInactiveCellBorderColor = Color.GREEN;
        final float kInactiveCellBorderWidth = 5.f;

        Paint paint = new Paint();
        paint.setTextSize(20.f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect textRect = new Rect();

        _factorW = getWidth() / _fieldW;
        _factorH = getHeight() / _fieldH;

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

                path.reset();
                PointF[] cellPoints = oneCell.points();

                // move cursor to the top left point
                PointF ppt = cellPoints[0]; // projected point
                path.moveTo(ppt.x * _factorW, ppt.y * _factorH);

                //drawing lined in the counterclock direction to avoid additinal line from the last to the first point
                for (int idx = cellPoints.length - 1; idx >= 0; idx--) {
                    ppt = cellPoints[idx];
                    path.lineTo(ppt.x * _factorW, ppt.y * _factorH);
                }
                canvas.drawPath(path, paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.description();
                paint.getTextBounds(oneCell.description(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.description(),
                        (oneCell.left() + oneCell.width()/2) * _factorW,
                        (oneCell.bottom() - textRect.height()) * _factorH,
                        paint);
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

                path.reset();
                PointF[] cellPoints = oneCell.points();
                // move cursor to the top left point
                PointF ppt = cellPoints[0]; // projected point
                path.moveTo(ppt.x * _factorW, ppt.y * _factorH);

                //drawing lined in the counterclock direction to avoid additinal line from the last to the first point
                for (int idx = cellPoints.length - 1; idx >= 0; idx--) {
                    ppt = cellPoints[idx];
                    path.lineTo(ppt.x * _factorW, ppt.y * _factorH);
                }

                canvas.drawPath(path, paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.description();
                paint.getTextBounds(oneCell.description(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.description(),
                        (oneCell.left() + oneCell.width()/2) * _factorW,
                        (oneCell.bottom() - textRect.height()) * _factorH,
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
        this._cells = cells;
    }

}
