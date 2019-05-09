select  *
from    tp_preferences;


ALTER TABLE tp_preferences
  drop COLUMN check_chart_type;
  

ALTER TABLE tp_preferences
  add COLUMN check_chart_type boolean DEFAULT false NOT NULL
  
