package org.tohu.test.element;


import org.tohu.test.AbstractSeleniumTest.WidgetType;

/**
 * Factory method to create Element objects
 * 
 * @author rb1317
 *
 */
public class ElementFactory {

    public static Element getElement(WidgetType widgetType) {
        switch (widgetType) {
            case CHECKBOX : 
                return new CheckboxElement();
            case DATEPICKER : 
                return new DatePickerElement();
            case TEXT :
                return new TextElement();
            case FILE :
                return new FileElement();
            case RADIO_BOOLEAN:
                return new RadioBooleanElement();
            case RADIO_GROUP:
                return new RadioGroupElement();
            case DROPDOWN:
                return new DropDownElement();
            case TEXTAREA:
                return new TextAreaElement();
            default:
                return new TextElement();                
        }
    }
    
}
