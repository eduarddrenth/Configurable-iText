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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.vectorprint.VectorPrintException;
import com.vectorprint.report.data.DataCollectionMessages;
import com.vectorprint.report.data.ReportDataHolder;
import com.vectorprint.report.itext.BaseReportGenerator;
import com.vectorprint.report.itext.DefaultElementProducer;
import com.vectorprint.report.itext.EventHelper;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class TestableReportGenerator extends BaseReportGenerator<ReportDataHolder> {


   public TestableReportGenerator() throws VectorPrintException {
      super(new EventHelper<>(), new DefaultElementProducer());
   }

   @Override
   protected void createReportBody(Document document, ReportDataHolder data, com.itextpdf.text.pdf.PdfWriter writer) throws DocumentException, VectorPrintException {
      try {
         
         createAndAddElement(null, getStylers("qr"), Image.class);

      } catch (InstantiationException | IllegalAccessException ex) {
         throw new VectorPrintException(ex);
      }
   }

   @Override
   public boolean continueOnDataCollectionMessages(DataCollectionMessages messages, com.itextpdf.text.Document document) throws VectorPrintException {
      if (!messages.getMessages(DataCollectionMessages.Level.ERROR).isEmpty()) {
         try {
            createAndAddElement(messages.getMessages(DataCollectionMessages.Level.ERROR), getStylers("bigbold"), Phrase.class);
         } catch (InstantiationException | IllegalAccessException | DocumentException ex) {
            throw new VectorPrintException(ex);
         }
      }
      return true;
   }

}
