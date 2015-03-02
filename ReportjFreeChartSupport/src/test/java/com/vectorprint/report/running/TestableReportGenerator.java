/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.report.running;

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

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.vectorprint.VectorPrintException;
import com.vectorprint.VectorPrintRuntimeException;
import com.vectorprint.report.data.DataCollectionMessages;
import com.vectorprint.report.data.ReportDataHolder;
import com.vectorprint.report.itext.BaseReportGenerator;
import com.vectorprint.report.itext.DefaultElementProducer;
import com.vectorprint.report.itext.EventHelper;
import com.vectorprint.report.itext.style.BaseStyler;
import com.vectorprint.report.itext.style.StyleHelper;
import com.vectorprint.report.itext.style.stylers.Chart;
import java.util.List;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.junit.Assert;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class TestableReportGenerator extends BaseReportGenerator<ReportDataHolder> {

   private static boolean didCreate = false;

   public TestableReportGenerator() throws VectorPrintException {
      super(new EventHelper<ReportDataHolder>(), new DefaultElementProducer());
      didCreate = false;
   }

   @Override
   protected void createReportBody(Document document, ReportDataHolder data, com.itextpdf.text.pdf.PdfWriter writer) throws DocumentException, VectorPrintException {
      didCreate = true;
      try {
         Dataset ds = new DefaultCategoryDataset();
         Chart c = getChartStylers("area");
         c.setData(ds);
         c.draw(new Rectangle(40, 40, 100, 100),"");
         document.newPage();
         
         createAndAddElement(ds, getStylers("area"), Image.class);
         createAndAddElement(ds, getStylers("bar"), Image.class);
         createAndAddElement(ds, getStylers("bar3d"), Image.class);
         createAndAddElement(ds, getStylers("line"), Image.class);
         createAndAddElement(ds, getStylers("line3d"), Image.class);
         createAndAddElement(new DefaultPieDataset(), getStylers("pie"), Image.class);
         createAndAddElement(new DefaultPieDataset(), getStylers("pie3d"), Image.class);
         createAndAddElement(new TimeSeriesCollection(), getStylers("time"), Image.class);
         createAndAddElement(new DefaultXYDataset(), getStylers("xyarea"), Image.class);
         createAndAddElement(new DefaultXYDataset(), getStylers("xyline"), Image.class);
         createAndAddElement(null, getStylers("xyline"), Image.class);
         
      } catch (InstantiationException ex) {
         throw new VectorPrintException(ex);
      } catch (IllegalAccessException ex) {
         throw new VectorPrintException(ex);
      }
   }

   @Override
   public boolean continueOnDataCollectionMessages(DataCollectionMessages messages, com.itextpdf.text.Document document) throws VectorPrintException {
      if (!messages.getMessages(DataCollectionMessages.Level.ERROR).isEmpty()) {
         try {
            createAndAddElement(messages.getMessages(DataCollectionMessages.Level.ERROR), getStylers("bigbold"), Phrase.class);
         } catch (InstantiationException ex) {
            throw new VectorPrintException(ex);
         } catch (IllegalAccessException ex) {
            throw new VectorPrintException(ex);
         } catch (DocumentException ex) {
            throw new VectorPrintException(ex);
         }
      }
      return true;
   }

   public static boolean isDidCreate() {
      return didCreate;
   }

   public static void setDidCreate(boolean didCreate) {
      TestableReportGenerator.didCreate = didCreate;
   }

   private Chart getChartStylers(String name) throws VectorPrintException {
      List<BaseStyler> stylers = getStylers(name);
      for (Chart cs : StyleHelper.getStylers(stylers, Chart.class)) {
         return cs;
      }
      return null;
   }

}
