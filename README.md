# RSS to Telegram

This program is kind of technology demo using ClojureScript as "generic language"
and it was developed on a weekend especially to deliver updates
of YouTube channel [Математика - просто](youtube.com/punkmathematics)
to Telegram channel [@savvateev_xyz](t.me/savvateev_xyz).

It supposed to be started periodically as a cron job.
Default config file name is `./rss2tlg.edn` but could be changed by `CONFIG_EDN` environment.
Config parser uses environment variables for `${...}` template substitutions.
Empty data file `./rss2tlg.data` required at first start.
See other runtime parameters in source file `config.cljs`.

Example:

```edn
{
  :apikey "${APIKEY}"
  :channel "@savvateev_xyz"
  :rss-url "https://www.youtube.com/feeds/videos.xml?channel_id=UClTIrwj5npeOaBjH6_AkKyA"
  :rss-num 1
}
```

crontab:  

```bash
  */5  *  *  *  *  cd /path/to/workdir && ./rss2tlg.js
```

---

- [maxp.dev](https://maxp.dev)
- vk.com/alexey_savvateev
- youtube.com/feeds/videos.xml?channel_id=UClTIrwj5npeOaBjH6_AkKyA
