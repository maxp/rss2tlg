
(ns app.config)

(def config
  {
    :rss-url    "${RSS_URL}"
    :rss-max    "${RSS_MAX:20}"
    :rss-num    "${RSS_NUM:2}"

    :data-file  "${DATA_FILE:./rss2telegram.data}"
    :data-max   "${DATA_MAX:100}"

    :apikey     "${APIKEY}"
    :channel    "${CHANNEL}"})
;



;;.
