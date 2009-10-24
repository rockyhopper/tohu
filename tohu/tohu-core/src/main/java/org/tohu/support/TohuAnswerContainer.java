package org.tohu.support;

import java.math.BigDecimal;
import java.util.Date;

public interface TohuAnswerContainer {
	
	public String getTextAnswer();

	public void setTextAnswer(String textAnswer);

	public Long getNumberAnswer();

	public void setNumberAnswer(Long numberAnswer);

	public BigDecimal getDecimalAnswer();

	public void setDecimalAnswer(BigDecimal decimalAnswer);

	public Boolean getBooleanAnswer();

	public void setBooleanAnswer(Boolean booleanAnswer);

	public Date getDateAnswer();

	public void setDateAnswer(Date dateAnswer);

	public boolean isAnswered();

	public void clearAnswer();

}
