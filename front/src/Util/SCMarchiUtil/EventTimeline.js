import React from "react";

export default function EventTimeline({ timelineData }) {
  return (
    <div className="overflow-y-auto max-h-[300px] space-y-2">
      {timelineData.map((event, index) => (
        <div key={index} className="border-b pb-2">
          <p className="text-sm text-gray-700">
            <span className="font-semibold">{event.eventTime}</span> - {event.hubName} - {event.eventType}
          </p>
        </div>
      ))}
    </div>
  );
}
