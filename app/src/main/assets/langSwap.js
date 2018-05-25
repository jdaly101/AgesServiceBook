function swapLang(myRow) {
	$(myRow.cells).toggle();
}

$("tr:has(p.alttext,p.chant,p.heirmos,p.hymn,p.hymnlinefirst,p.hymnlinemiddle,p.hymnlinelast,p.prayer,p.prayerzero,p.verse,p.versecenter,p.inaudible,p.dialog,p.dialogzero,p.reading,p.readingzero,p.readingcenter,p.readingcenterzero,p.rubric,.media-group)").attr("onclick","swapLang(this)");