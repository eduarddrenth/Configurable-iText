package com.vectorprint.report.itext.style;

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
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.vectorprint.VectorPrintException;
import com.vectorprint.VectorPrintRuntimeException;
import com.vectorprint.configuration.EnhancedMap;
import com.vectorprint.configuration.annotation.Setting;
import com.vectorprint.configuration.annotation.SettingsField;
import com.vectorprint.configuration.binding.parameters.ParamBindingService;
import com.vectorprint.configuration.binding.parameters.ParameterizableBindingFactory;
import com.vectorprint.configuration.binding.parameters.ParameterizableParser;
import com.vectorprint.configuration.binding.settings.SettingsBindingService;
import com.vectorprint.configuration.parameters.Parameterizable;
import com.vectorprint.report.ReportConstants;
import static com.vectorprint.report.ReportConstants.DEBUG;
import com.vectorprint.report.itext.ElementProducer;
import com.vectorprint.report.itext.ImageLoader;
import com.vectorprint.report.itext.LayerManager;
import com.vectorprint.report.itext.EventHelper;
import com.vectorprint.report.itext.debug.DebugStyler;
import com.vectorprint.report.itext.style.conditions.AbstractCondition;
import com.vectorprint.report.itext.style.stylers.Advanced;
import com.vectorprint.report.itext.style.stylers.Font;
import com.vectorprint.report.itext.style.stylers.ImportPdf;
import com.vectorprint.report.itext.style.stylers.ImportTiff;
import com.vectorprint.report.itext.style.stylers.SimpleColumns;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

//~--- JDK imports ------------------------------------------------------------
public class DefaultStylerFactory implements StylerFactory, ConditionFactory {

   /**
    * name of the optional property containing advanced stylers that will be executed for each page
    */
   public static final String PAGESTYLERS = "PAGESTYLERS";
   /**
    * name of the optional property containing stylers that will be executed before all other stylers
    *
    * @see #PREANDPOSTSTYLE
    */
   public static final String PRESTYLERS = "PRESTYLERS";
   /**
    * name of the optional property containing stylers that will be executed after all other stylers
    *
    * @see #PREANDPOSTSTYLE
    */
   public static final String POSTSTYLERS = "POSTSTYLERS";
   /**
    * The default document will be A4 with margins of 25 mm when a setting {@link ReportConstants#DOCUMENTSETTINGS} is
    * not defined
    */
   public static final String DEFAULTDOCUMENTSETTINGS = "DocumentSettings(margin_top=25,margin_left=25,margin_right=25,margin_bottom=25,width=297,height=210)";
   private Map<String, String> styleSetup = new HashMap<String, String>(100);
   /**
    * put your own stylers in this package
    */
   public static final String STYLERPACKAGENAME = Font.class.getPackage().getName();
   /**
    * put your own conditions in this package
    */
   public static final String CONDITIONPACKAGENAME = AbstractCondition.class.getPackage().getName();
   private static final Logger log = Logger.getLogger(DefaultStylerFactory.class.getName());
   private Map<String, Object> cache = new HashMap<String, Object>(100);
   @SettingsField
   private EnhancedMap settings;
   private Document document;
   private PdfWriter writer;
   private ImageLoader imageLoader;
   private LayerManager layerManager;
   private static final ParameterizableBindingFactory BINDING_FACTORY = ParamBindingService.getInstance().getFactory();
   /**
    * name of the boolean setting use {@link #PRESTYLERS} and {@link #POSTSTYLERS} or not
    */
   public static final String PREANDPOSTSTYLE = "preandpoststyle";
   @Setting(keys = PREANDPOSTSTYLE)
   private boolean doFirstLast = true;

   private ParameterizableParser getParser(StringReader sr) {
      return BINDING_FACTORY.getParser(sr);
   }

   @Override
   public DocumentStyler getDocumentStyler() throws VectorPrintException {
      String className = settings.getProperty(DEFAULTDOCUMENTSETTINGS, ReportConstants.DOCUMENTSETTINGS);
      if (settings.getBooleanProperty(Boolean.FALSE, DEBUG)) {
         styleSetup.put(ReportConstants.DOCUMENTSETTINGS, className);
         DocumentStyler ds = (DocumentStyler) getParser(new StringReader(className))
             .setSettings(settings).setPackageName(STYLERPACKAGENAME)
             .parseParameterizable();
         StylerFactoryHelper.initStylingObject(ds, writer, document, imageLoader, layerManager, settings);
         Collection<BaseStyler> c = new ArrayList<BaseStyler>(1);
         c.add((BaseStyler) ds);
         cache.put(ReportConstants.DOCUMENTSETTINGS, c);
         return ds;
      } else {
         DocumentStyler ds = (DocumentStyler) getParser(new StringReader(className))
             .setSettings(settings).setPackageName(STYLERPACKAGENAME)
             .parseParameterizable();
         StylerFactoryHelper.initStylingObject(ds, writer, document, imageLoader, layerManager, settings);
         return ds;
      }
   }

   private final List<ImportPdf> impdf = new ArrayList<>(1);
   private final List<ImportTiff> imtiff = new ArrayList<>(1);
   private final List<SimpleColumns> imcol = new ArrayList<>(1);

   private <S extends Parameterizable> List<S> getStylers(String key, String pkg )
       throws VectorPrintException {

      if (!cache.containsKey(key)) {

         String[] classNames = settings.getStringProperties(null, key);
         if (classNames == null) {
            classNames = new String[0];
         }
         List<S> parameterizables = new ArrayList<>(classNames.length);

         for (String classNameWithParams : classNames) {
            if (classNameWithParams.isEmpty()) {
               continue;
            }
            Parameterizable parameterizable = getParameterizable(classNameWithParams, pkg);
            if (parameterizable instanceof BaseStyler) {
               ((BaseStyler) parameterizable).setStyleClass(key);
            } else {
               ((StylingCondition) parameterizable).setConfigKey(key);
            }
            parameterizables.add((S) parameterizable);
         }

         cache.put(key, parameterizables);
         return parameterizables;
      } else {
         return (List<S>) cache.get(key);
      }

   }

   /**
    * turns a configuration like FieldFont(family=verdana) into a styler
    *
    * @param <S>
    * @param classNameWithParams
    * @param clazz
    * @param pkg
    * @return
    * @throws VectorPrintException
    */
   private <S extends Parameterizable> S getParameterizable(String classNameWithParams, String pkg) throws VectorPrintException {
      // TODO this will trigger a call to update in AbstractStyler where a ConditionFactory is needed but not yet provided
      S st = (S) getParser(new StringReader(classNameWithParams))
          .setSettings(settings).setPackageName(pkg)
          .parseParameterizable();
      StylerFactoryHelper.initStylingObject(st, writer, document, imageLoader, layerManager, settings, (ElementProducer) imageLoader, this, this);
      if (st instanceof ImportPdf) {
         impdf.add((ImportPdf) st);
      } else if (st instanceof BaseStyler) {
         for (ImportPdf ipdf : impdf) {
            ipdf.addStyler((BaseStyler) st);
         }
      }
      if (st instanceof SimpleColumns) {
         imcol.add((SimpleColumns) st);
      } else if (st instanceof BaseStyler) {
         for (SimpleColumns sc : imcol) {
            sc.addStyler((BaseStyler) st);
         }
      }
      if (st instanceof ImportTiff) {
         imtiff.add((ImportTiff) st);
      } else if (st instanceof BaseStyler) {
         for (ImportTiff ipt : imtiff) {
            ipt.addStyler((BaseStyler) st);
         }
      }
      return st;
   }

   /**
    * return a debug styler that will be appended to the stylers for providing debugging info in reports
    *
    * @param names
    * @return
    */
   private DebugStyler debugStylers(String... names) throws VectorPrintException {
      if (settings.getBooleanProperty(false, DEBUG)) {
         DebugStyler dst = new DebugStyler();
         StylerFactoryHelper.initStylingObject(dst, writer, document, null, layerManager, settings);
         for (String n : names) {
            dst.getStyleSetup().add(n);
            styleSetup.put(n, SettingsBindingService.getInstance().getFactory().getBindingHelper().serializeValue(settings.getStringProperties(null, n)));
         }

         return dst;
      }

      return null;
   }

   /**
    *
    * @param styleClasses
    * @return
    * @throws VectorPrintException
    */
   @Override
   public List<BaseStyler> getStylers(String... styleClasses) throws VectorPrintException {
      List<BaseStyler> stylers = preStyle(new ArrayList<BaseStyler>(styleClasses.length + 4), styleClasses);

      for (String name : styleClasses) {
         stylers.addAll(getStylers(name, STYLERPACKAGENAME));
      }

      postStyle(stylers, styleClasses);
      return stylers;
   }

   private <B extends Parameterizable> void debug(List<B> stylers, String... styleClasses) throws VectorPrintException {
      if (settings.getBooleanProperty(false, DEBUG)) {
         for (String clazz : styleClasses) {
            if (!cache.containsKey(clazz)) {
               cache.put(clazz, stylers);
            }
         }
         int s = (settings.containsKey(POSTSTYLERS))
             ? (settings.containsKey(PRESTYLERS)) ? 2 : 1
             : (settings.containsKey(PRESTYLERS)) ? 1 : 0;
         String[] styleNames = Arrays.copyOf(styleClasses, styleClasses.length + s);
         if (settings.containsKey(PRESTYLERS)) {
            styleNames[styleClasses.length] = PRESTYLERS;
         }
         if (settings.containsKey(POSTSTYLERS)) {
            styleNames[styleClasses.length + 1] = POSTSTYLERS;
         }
         DebugStyler ds = debugStylers(styleNames);

         if (ds != null) {
            stylers.add((B) ds);
         }
      }

   }

   @Override
   public Map<String, String> getStylerSetup() {
      return styleSetup;
   }

   @Override
   public void setDocument(Document document, com.itextpdf.text.pdf.PdfWriter writer) {
      this.document = document;
      this.writer = writer;
      if (settings.containsKey(PAGESTYLERS)) {
         try {
            EventHelper ph = (EventHelper) writer.getPageEvent();
            // init page stylers
            Collection<BaseStyler> p = getStylers(PAGESTYLERS, STYLERPACKAGENAME);
            for (BaseStyler s : p) {
               if (s instanceof Advanced) {
                  ph.addStylerForEachPage((Advanced) s);
               } else {
                  log.warning(s.getClass().getSimpleName() + " is not an advanced styler, cannot be used for each page");
               }
            }
            styleSetup.put(PAGESTYLERS, BINDING_FACTORY.getBindingHelper().serializeValue(settings.getStringProperties(null, PAGESTYLERS)));
         } catch (VectorPrintException ex) {
            throw new VectorPrintRuntimeException(ex);
         }
      }
   }

   @Override
   public void setImageLoader(ImageLoader imageLoader) {
      this.imageLoader = imageLoader;
   }

   @Override
   public void setLayerManager(LayerManager layerManager) {
      this.layerManager = layerManager;
   }

   private boolean containsFirstLast(String... styleClasses) {
      boolean containsFirstLast = false;
      for (String n : styleClasses) {
         if (PRESTYLERS.equals(n)) {
            containsFirstLast = true;
            break;
         }
         if (POSTSTYLERS.equals(n)) {
            containsFirstLast = true;
            break;
         }
      }
      return containsFirstLast;
   }

   private <B extends Parameterizable> List<B> preStyle(List<B> stylers, String... styleClasses) throws VectorPrintException {

      if (doFirstLast && !containsFirstLast(styleClasses) && settings.containsKey(PRESTYLERS)) {
         stylers.addAll((Collection<? extends B>) getStylers(PRESTYLERS, STYLERPACKAGENAME));
      }
      return stylers;
   }

   private <B extends Parameterizable> void postStyle(List<B> stylers, String... styleClasses) throws VectorPrintException {
      if (doFirstLast && !containsFirstLast(styleClasses) && settings.containsKey(POSTSTYLERS)) {
         stylers.addAll((Collection<? extends B>) getStylers(POSTSTYLERS, STYLERPACKAGENAME));
      }

      impdf.clear();
      imtiff.clear();
      imcol.clear();

      debug(stylers, styleClasses);
   }

   @Override
   public List<BaseStyler> getBaseStylersFromCache(String... styleClasses) throws VectorPrintException {
      List<BaseStyler> stylers = new ArrayList<BaseStyler>(styleClasses.length + 4);
      for (String s : styleClasses) {
         stylers.addAll((Collection<BaseStyler>) cache.get(s));
      }
      return stylers;
   }

   @Override
   public Document getDocument() {
      return document;
   }

   @Override
   public PdfWriter getWriter() {
      return writer;
   }

   @Override
   public LayerManager getLayerManager() {
      return layerManager;
   }

   public EnhancedMap getSettings() {
      return settings;
   }

   @Override
   public List<StylingCondition> getConditions(String configKey) throws VectorPrintException {
      return getStylers(configKey, CONDITIONPACKAGENAME);
   }

}
