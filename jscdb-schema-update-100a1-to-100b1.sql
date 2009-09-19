-- memberstatus -- a view of membership reflecting payment status
CREATE OR REPLACE VIEW memberstatus AS
 SELECT m.mid, m.pid, m.paymentid, m.season, p.paypal_status
   FROM membership m, payment p
  WHERE m.paymentid = p.paymentid;