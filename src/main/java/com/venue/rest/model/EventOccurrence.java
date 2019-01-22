package com.venue.rest.model;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

public class EventOccurrence {
	private String eventStartDate = "";
	private String eventStopDate = "";
	
	@JsonInclude(Include.NON_DEFAULT)
	private String ticketURL = "";
	
	public String getEventStartDate() {
		return eventStartDate;
	}
	public void setEventStartDate(String eventStartDate) {
		this.eventStartDate = eventStartDate;
	}
	public String getEventStopDate() {
		return eventStopDate;
	}
	public void setEventStopDate(String eventStopDate) {
		this.eventStopDate = eventStopDate;
	}
	public String getTicketURL() {
		return ticketURL;
	}
	public void setTicketURL(String ticketURL) {
		this.ticketURL = ticketURL;
	}
}
