
(ns app.config)

(def config
  {
    :rss-url    "${RSS_URL}"
    :rss-max    "${RSS_MAX:20}"
    :rss-num    "${RSS_NUM:1}"

    :data-file  "${DATA_FILE:./rss2tlg.data}"
    :data-max   "${DATA_MAX:100}"

    :apikey     "${APIKEY}"
    :channel    "${CHANNEL}"})
;



;;.
