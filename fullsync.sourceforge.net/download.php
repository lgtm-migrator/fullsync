<?php
	include( "html.php" );
	HtmlHeader( "Download" );
	$version = '0.10.0';
?>
	<h1>Download FullSync</h1>

	<h2>for Windows</h2>
	<p>
		Tested with Windows XP, and Windows 7. Java must be installed, you can get it at <a href="http://www.java.com/">www.java.com</a>
		<a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>-Windows.msi/download">Download FullSync for Windows</a>
	</p>

	<h2>for Linux</h2>
	<iframe id="obs-download-frame" src="http://software.opensuse.org/download/package.iframe?project=home%3Acobexer%3AFullSync&amp;package=FullSync"></iframe>

	<h2>other Versions</h2>
	<p>
		This download can be used on Windows, Linux and (in theory) on Mac.<br />
		It contains both 32bit and 64bit versions of FullSync.
		<a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>.zip/download">Platform independent download</a>
	</p>

	<h2>Source Code</h2>
	<p>
		The source code of FullSync Version <?php echo $version; ?> can be obtained here:
		<a href="http://sourceforge.net/projects/fullsync/files/FullSync%20<?php echo $version; ?>/FullSync-<?php echo $version; ?>-src.tar.gz/download">FullSync-<?php echo $version; ?>-src.tar.gz</a><br />
		To access the FullSync SVN repository head over to <a href="http://sourceforge.net/scm/?type=svn&amp;group_id=115436">here</a>.
	</p>

	<h2>Older versions</h2>
	<p>
		You can get older versions of FullSync from <a href="http://sourceforge.net/projects/fullsync/files/">http://sourceforge.net/projects/fullsync/files/</a>.
	</p>

<?php
	HtmlFooter();