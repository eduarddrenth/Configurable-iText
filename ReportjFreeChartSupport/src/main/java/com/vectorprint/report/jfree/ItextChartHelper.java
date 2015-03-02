package com.vectorprint.report.jfree;

/*
 * #%L
 * VectorPrintReport4.0
 * %%
 * Copyright (C) 2012 - 2013 VectorPrint
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
//~--- non-JDK imports --------------------------------------------------------
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfTemplate;

import org.jfree.chart.*;

//~--- JDK imports ------------------------------------------------------------
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Helper that can plot a JFreeChart on a PdfContentByte while preserving the quality of the graphics.
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ItextChartHelper {

   /**
    *
    * @param chart the chart tp plot
    * @param contentByte the canvas to plot to
    * @param width the width of the chart will be fit to
    * @param height the height of the chart will be fit to
    * @return
    * @throws BadElementException
    */
   public static Image getChartImage(JFreeChart chart, PdfContentByte contentByte, float width, float height, float opacity)
       throws BadElementException {

      // create PdfTemplate from PdfContentByte
      PdfTemplate template = contentByte.createTemplate(width, height);
      template.saveState();
      PdfGState pgs = new PdfGState();
      pgs.setFillOpacity(opacity);
      pgs.setStrokeOpacity(opacity);
      template.setGState(pgs);

      // create Graphics2D from PdfTemplate
      Graphics2D g2 = new PdfGraphics2D(template, width, height);

      // setup the drawing area
      Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);

      // pass the Graphics2D and drawing area to JFreeChart
      chart.draw(g2, r2D);
      g2.dispose();    // always dispose this

      template.restoreState();

      // create Image from PdfTemplate
      return Image.getInstance(template);
   }
}
