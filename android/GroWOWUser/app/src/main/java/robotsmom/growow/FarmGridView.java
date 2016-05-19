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

    // coordinates of grid corners after skewing it inside of linear rectangle.
    // To recalculate coordinates of cells and rest of stuff
    private
    PointF _perspectiveLT, // left top
            _perspectiveRT, // right top
            _perspectiveLB, // left bottom
            _perspectiveRB; // right bottom

    private float _maxShiftX;
    private double _topAngleSin;
    private double _bottomAngleSin;

    public FarmGridView(Context context) {
        super(context);
    }

    public FarmGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FarmGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // set perspective vertices of the grid.
    // wanted to transform cells while drawing in accordance to camera position
    // At the moment for the simplisity will assume that we have only isosceles trapezoid with bottom grid line as base
    // So, will operate only with top horizontal shift. Y coordinate is assumed to not be changed
    public void setPerspective(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3)
    {
        setPerspective(new PointF(x0, y0),
                        new PointF(x1, y1),
                        new PointF(x2, y2),
                        new PointF(x3, y3)
                );
    }

    public void setPerspective(PointF lt, PointF rt, PointF lb, PointF rb)
    {
        _perspectiveLT = lt;
        _perspectiveRT = rt;
        _perspectiveLB = lb;
        _perspectiveRB = rb;

    }

    // projecting point from rectangular grid to perspectived
    private PointF projectPoint(PointF point)
    {
        PointF newPoint = new PointF(point.x, point.y); // Y is not changed yet
        double deltaX = _perspectiveLT.x - (point.y * _topAngleSin / _bottomAngleSin); // delta x for current Y

        // if the point located before vertical grid center, add the delta. Otherwise subtract it.
        if( point.x < this.getWidth()/2.f ) {
            newPoint.offset((float) deltaX, 0);
        }
        else {
            newPoint.offset((float) -deltaX, 0);
        }

        return newPoint;
    }

    // fills cells array with fake cells. Just for test
    public void fillCells(int cols, int rows)
    {
        String[] vegetables = {"Carrot", "Tomato", "Berry", "Cucumber", "Ukrop", "Patison", "Corn", "Pumpkin"};
        _cells = new ArrayList<FarmCell>();
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

                _cells.add(newCell);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        Log.d(LOG_TAG, "Got touch in (" + event.getX() + ", " + event.getY() + ")");
        Log.d(LOG_TAG, "view.Left(" + view.getLeft() + "), Right(" + view.getRight() + ")" + ", Top(" + view.getTop() + ")");

        //drawGrid(this, 5, 3, event.getX(), event.getY());
        if( null == _cells ) {
            fillCells(5, 3);
        }

        PointF point = new PointF(event.getX(), event.getY());

        // find touched cell
        for (FarmCell oneCell:_cells) {
            oneCell.setSelected( oneCell.containsPoint(point) );
        }

        drawCells(_cells);

        return false;
    }


    public void drawCells(ArrayList<FarmCell> cells)
    {
        // do these calculations here for a while

        // magical sinuses
        Log.d(LOG_TAG, "this.H/W(" + this.getHeight() + "," + this.getWidth() + ")" );
        double hypo = Math.sqrt( Math.pow(this.getHeight(), 2) + Math.pow(_perspectiveLT.x, 2) );
        _topAngleSin = _perspectiveLT.x / hypo; // top angle sinus
        _bottomAngleSin = Math.sin( (180 - 90) - Math.asin(_topAngleSin) ); // bottom angle sinus


        //=======================================================================================

        final int kActiveCellBorderColor = Color.RED;
        final float kActiveCellBorderWidth = 10.f;
        final int kInactiveCellBorderColor = Color.GREEN;
        final float kInactiveCellBorderWidth = 5.f;

        Paint paint = new Paint();
        paint.setTextSize(60.f);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect textRect = new Rect();

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
                PointF ppt = projectPoint(cellPoints[0]); // projected point
                path.moveTo(ppt.x, ppt.y);

                //drawing lined in the counterclock direction to avoid additinal line from the last to the first point
                for (int idx = cellPoints.length - 1; idx >= 0; idx--) {
                    ppt = projectPoint(cellPoints[idx]);
                    path.lineTo(ppt.x, ppt.y);
                }
                canvas.drawPath(path, paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.description();
                paint.getTextBounds(oneCell.description(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.description(),
                        oneCell.left() + oneCell.width()/2,
                        oneCell.bottom() - textRect.height(),
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
                PointF ppt = projectPoint(cellPoints[0]); // projected point
                path.moveTo(ppt.x, ppt.y);

                //drawing lined in the counterclock direction to avoid additinal line from the last to the first point
                for (int idx = cellPoints.length - 1; idx >= 0; idx--) {
                    ppt = projectPoint(cellPoints[idx]);
                    path.lineTo(ppt.x, ppt.y);
                }

                canvas.drawPath(path, paint);

                // love with text drawing
                paint.setStyle(Paint.Style.FILL);
                String descr = oneCell.description();
                paint.getTextBounds(oneCell.description(), 0, descr.length(), textRect);
                canvas.drawText(oneCell.description(),
                        oneCell.left() + oneCell.width()/2,
                        oneCell.bottom() - textRect.height(),
                        paint);
            }
        }
        finally {
            this.unlockCanvasAndPost(canvas);
        }
    }

    public void drawGrid(FarmGridView txv, int cols, int rows, float selX, float selY)
    {
        Paint paint = new Paint();
        paint.setColor(0xff00ff00);
        paint.setStrokeWidth(5.f);
        final Canvas canvas = txv.lockCanvas();
        try
        {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            for( float idx = 0; idx <= txv.getWidth(); idx += txv.getWidth()/cols) {
                canvas.drawLine(idx, 0, idx, txv.getHeight(), paint);
            }

            for( float idx = 0; idx <= txv.getHeight(); idx += txv.getHeight()/rows) {
                canvas.drawLine(0, idx, txv.getWidth(), idx, paint);
            }
        }
        finally {
            txv.unlockCanvasAndPost(canvas);
        }
    }
}
