package com.amra.ServicesStatusTracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Widgetset("com.amra.ServicesStatusTracker.MyAppWidgetset")
public class ServiceStatusTrackerUI extends UI {

	private static final String ServiceFileName = "ServiceDetails.properties";

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();

		// Create an empty tab sheet.
		TabSheet tabsheet = new TabSheet();

		createLayoutWithTable(new VerticalLayout(), tabsheet, "OWG");
		createLayoutWithTable(new VerticalLayout(), tabsheet, "CPM");
		layout.addComponents(tabsheet);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
	}

	@WebServlet(urlPatterns = "/*", name = "ServiceStatusTrackerUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = ServiceStatusTrackerUI.class, productionMode = false)
	public static class ServiceStatusTrackerUIServlet extends VaadinServlet {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	}

	public Table SetTable(String serviceArea) {
		Table table = new Table();

		// Define two columns for the built-in container
		table.addContainerProperty("Environment", String.class, null);
		table.addContainerProperty("Service Name", String.class, null);
		table.addContainerProperty("Service Type", String.class, null);
		table.addContainerProperty("Service Status", String.class, null);

		Properties prop = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(serviceArea + ServiceFileName);
		try {
			if (null != inputStream) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("File Not found" + serviceArea + ServiceFileName);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Enumeration enuKeys = prop.keys();
		int i = 1;
		while (enuKeys.hasMoreElements()) {
			String key = (String) enuKeys.nextElement();
			String value = prop.getProperty(key);
			System.out.println(key + ": " + value);
			List<String> serviceList = Arrays.asList(value.split(","));
			// table.addItem(new Object[] { serviceList.get(0),
			// serviceList.get(1), serviceList.get(2), "Running" }, i);
			table.addItem(new Object[] { serviceList.get(0), serviceList.get(1), serviceList.get(2),
					PingService.pingURL(serviceList.get(3), 10) }, i);
			table.setStyleName("serviceTable.css");
			table.setCellStyleGenerator(new Table.CellStyleGenerator() {

				@Override
				public String getStyle(Table source, Object itemId, Object propertyId) {

					if (propertyId == null) {
						// Styling for row
						Item item = table.getItem(itemId);
						String serviceStatus = (String) item.getItemProperty("Service Status").getValue();
						if (serviceStatus.equals("red")) {
							System.out.println("Red");
							return "highlight-red";
							
						} else {
							return "highlight-green";
						}
					} else {
						// styling for column propertyId
						return null;
					}
				}
			});
			i++;
		}

		// Show exactly the currently contained rows (items)
		table.setPageLength(table.size());
		return table;
	}

	public void createLayoutWithTable(VerticalLayout layout, TabSheet tabsheet, String serviceArea) {

		layout.addComponent(new Label("Status of " + serviceArea + " services!"));
		tabsheet.addTab(layout);
		tabsheet.getTab(layout).setCaption(serviceArea);

		Table table = SetTable(serviceArea);
		table.setSizeFull();
		layout.addComponent(table);
		layout.setExpandRatio(table, 1);

	}
}
