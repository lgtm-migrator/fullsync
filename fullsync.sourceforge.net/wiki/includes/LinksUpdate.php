<?php
# See deferred.doc

class LinksUpdate {

	/* private */ var $mId, $mTitle;

	function LinksUpdate( $id, $title )
	{
		$this->mId = $id;
		$this->mTitle = $title;
		$this->mTitleEnc = wfStrencode( $title );
	}

	
	function doUpdate()
	{
		global $wgUseBetterLinksUpdate, $wgLinkCache, $wgDBtransactions;
		global $wgEnablePersistentLC, $wgUseCategoryMagic;

		/* Update link tables with outgoing links from an updated article */
		/* Relies on the 'link cache' to be filled out */

		$fname = "LinksUpdate::doUpdate";
		wfProfileIn( $fname );

		$del = array();
		$add = array();

		if( $wgDBtransactions ) {
			$sql = "BEGIN";
			wfQuery( $sql, DB_WRITE, $fname );
		}
		
		#------------------------------------------------------------------------------
		# Good links

		if ( $wgLinkCache->incrementalSetup( LINKCACHE_GOOD, $del, $add ) ) {
			# Delete where necessary
			if ( count( $del ) ) {
				$sql = "DELETE FROM links WHERE l_from={$this->mId} AND l_to IN(".
					implode( ",", $del ) . ")";
				wfQuery( $sql, DB_WRITE, $fname );
			}
		} else {
			# Delete everything
			$sql = "DELETE FROM links WHERE l_from={$this->mId}";
			wfQuery( $sql, DB_WRITE, $fname );
			
			# Get the addition list
			$add = $wgLinkCache->getGoodLinks();
		}

		# Do the insertion
		$sql = "";
		if ( 0 != count( $add ) ) {
			# The link cache was constructed without FOR UPDATE, so there may be collisions
			# Ignoring for now, I'm not sure if that causes problems or not, but I'm fairly
			# sure it's better than without IGNORE
			$sql = "INSERT IGNORE INTO links (l_from,l_to) VALUES ";
			$first = true;
			foreach( $add as $lt => $lid ) {
				
				if ( ! $first ) { $sql .= ","; }
				$first = false;

				$sql .= "({$this->mId},{$lid})";
			}
		}
		if ( "" != $sql ) { 
			wfQuery( $sql, DB_WRITE, $fname ); 
		}

		#------------------------------------------------------------------------------
		# Bad links

		if ( $wgLinkCache->incrementalSetup( LINKCACHE_BAD, $del, $add ) ) {
			# Delete where necessary
			if ( count( $del ) ) {
				$sql = "DELETE FROM brokenlinks WHERE bl_from={$this->mId} AND bl_to IN('" . 	
					implode( "','", array_map( "wfStrencode", $del ) ) . "')";
				wfQuery( $sql, DB_WRITE, $fname );
			}
		} else {
			# Delete all
			$sql = "DELETE FROM brokenlinks WHERE bl_from={$this->mId}";
			wfQuery( $sql, DB_WRITE, $fname );
			
			# Get addition list
			$add = $wgLinkCache->getBadLinks();
		}

		# Do additions
		$sql = "";
		if ( 0 != count ( $add ) ) {
			$sql = "INSERT IGNORE INTO brokenlinks (bl_from,bl_to) VALUES ";
			$first = true;
			foreach( $add as $blt ) {
				$blt = wfStrencode( $blt );
				if ( ! $first ) { $sql .= ","; }
				$first = false;

				$sql .= "({$this->mId},'{$blt}')";
			}
		}
		if ( "" != $sql ) { 
			wfQuery( $sql, DB_WRITE, $fname );
		}

		#------------------------------------------------------------------------------
		# Image links
		$sql = "DELETE FROM imagelinks WHERE il_from='{$this->mId}'";
		wfQuery( $sql, DB_WRITE, $fname );
		
		# Get addition list
		$add = $wgLinkCache->getImageLinks();
		
		# Do the insertion
		$sql = "";
		$image = Namespace::getImage();
		if ( 0 != count ( $add ) ) {
			$sql = "INSERT IGNORE INTO imagelinks (il_from,il_to) VALUES ";
			$first = true;
			foreach( $add as $iname => $val ) {
				# FIXME: Change all this to avoid unnecessary duplication
				$nt = Title::makeTitle( $image, $iname );
				if( !$nt ) continue;
				$nt->invalidateCache();

				$iname = wfStrencode( $iname );
				if ( ! $first ) { $sql .= ","; }
				$first = false;

				$sql .= "({$this->mId},'{$iname}')";
			}
		}
		if ( "" != $sql ) { wfQuery( $sql, DB_WRITE, $fname ); }

		#------------------------------------------------------------------------------
		# Category links
		if( $wgUseCategoryMagic ) {
			$sql = "DELETE FROM categorylinks WHERE cl_from='{$this->mId}'";
			wfQuery( $sql, DB_WRITE, $fname );
			
			# Get addition list
			$add = $wgLinkCache->getCategoryLinks();
			
			# Do the insertion
			$sql = "";
			if ( 0 != count ( $add ) ) {
				$sql = "INSERT IGNORE INTO categorylinks (cl_from,cl_to,cl_sortkey) VALUES ";
				$first = true;
				foreach( $add as $cname => $sortkey ) {
					# FIXME: Change all this to avoid unnecessary duplication
					$nt = Title::makeTitle( NS_CATEGORY, $cname );
					if( !$nt ) continue;
					$nt->invalidateCache();
	
					if ( ! $first ) { $sql .= ","; }
					$first = false;
	
					$sql .= "({$this->mId},'" . wfStrencode( $cname ) .
						"','" . wfStrencode( $sortkey ) . "')";
				}
			}
			if ( "" != $sql ) { wfQuery( $sql, DB_WRITE, $fname ); }
		}
		
		$this->fixBrokenLinks();

		if( $wgDBtransactions ) {
			$sql = "COMMIT";
			wfQuery( $sql, DB_WRITE, $fname );
		}
		wfProfileOut( $fname );
	}
	
	function doDumbUpdate()
	{
		# Old inefficient update function
		# Used for rebuilding the link table
		
		global $wgLinkCache, $wgDBtransactions, $wgUseCategoryMagic;
		$fname = "LinksUpdate::doDumbUpdate";
		wfProfileIn( $fname );

		if( $wgDBtransactions ) {
			$sql = "BEGIN";
			wfQuery( $sql, DB_WRITE, $fname );
		}
		
		$sql = "DELETE FROM links WHERE l_from={$this->mId}";
		wfQuery( $sql, DB_WRITE, $fname );

		$a = $wgLinkCache->getGoodLinks();
		$sql = "";
		if ( 0 != count( $a ) ) {
			$sql = "INSERT IGNORE INTO links (l_from,l_to) VALUES ";
			$first = true;
			foreach( $a as $lt => $lid ) {
				if ( ! $first ) { $sql .= ","; }
				$first = false;

				$sql .= "({$this->mId},{$lid})";
			}
		}
		if ( "" != $sql ) { wfQuery( $sql, DB_WRITE, $fname ); }

		$sql = "DELETE FROM brokenlinks WHERE bl_from={$this->mId}";
		wfQuery( $sql, DB_WRITE, $fname );

		$a = $wgLinkCache->getBadLinks();
		$sql = "";
		if ( 0 != count ( $a ) ) {
			$sql = "INSERT IGNORE INTO brokenlinks (bl_from,bl_to) VALUES ";
			$first = true;
			foreach( $a as $blt ) {
				$blt = wfStrencode( $blt );
				if ( ! $first ) { $sql .= ","; }
				$first = false;

				$sql .= "({$this->mId},'{$blt}')";
			}
		}
		if ( "" != $sql ) { wfQuery( $sql, DB_WRITE, $fname ); }
		
		$sql = "DELETE FROM imagelinks WHERE il_from={$this->mId}";
		wfQuery( $sql, DB_WRITE, $fname );

		$a = $wgLinkCache->getImageLinks();
		$sql = "";
		if ( 0 != count ( $a ) ) {
			$sql = "INSERT IGNORE INTO imagelinks (il_from,il_to) VALUES ";
			$first = true;
			foreach( $a as $iname => $val ) {
				$iname = wfStrencode( $iname );
				if ( ! $first ) { $sql .= ","; }
				$first = false;

				$sql .= "({$this->mId},'{$iname}')";
			}
		}
		if ( "" != $sql ) { wfQuery( $sql, DB_WRITE, $fname ); }

		if( $wgUseCategoryMagic ) {
			$sql = "DELETE FROM categorylinks WHERE cl_from='{$this->mId}'";
			wfQuery( $sql, DB_WRITE, $fname );
			
			# Get addition list
			$add = $wgLinkCache->getCategoryLinks();
			
			# Do the insertion
			$sql = "";
			if ( 0 != count ( $add ) ) {
				$sql = "INSERT IGNORE INTO categorylinks (cl_from,cl_to,cl_sortkey) VALUES ";
				$first = true;
				foreach( $add as $cname => $sortkey ) {
					# FIXME: Change all this to avoid unnecessary duplication
					$nt = Title::makeTitle( NS_CATEGORY, $cname );
					if( !$nt ) continue;
					$nt->invalidateCache();
	
					if ( ! $first ) { $sql .= ","; }
					$first = false;
	
					$sql .= "({$this->mId},'" . wfStrencode( $cname ) .
						"','" . wfStrencode( $sortkey ) . "')";
				}
			}
			if ( "" != $sql ) { wfQuery( $sql, DB_WRITE, $fname ); }
		}
		$this->fixBrokenLinks();

		if( $wgDBtransactions ) {
			$sql = "COMMIT";
			wfQuery( $sql, DB_WRITE, $fname );
		}
		wfProfileOut( $fname );
	}
	
	function fixBrokenLinks() {
		/* Update any brokenlinks *to* this page */
		/* Call for a newly created page, or just to make sure state is consistent */
		$fname = "LinksUpdate::fixBrokenLinks";
		
		$sql = "SELECT bl_from FROM brokenlinks WHERE bl_to='{$this->mTitleEnc}'";
		$res = wfQuery( $sql, DB_READ, $fname );
		if ( 0 == wfNumRows( $res ) ) { return; }

		# Ignore errors. If a link existed in both the brokenlinks table and the links 
		# table, that's an error which can be fixed at this stage by simply ignoring collisions
		$sql = "INSERT IGNORE INTO links (l_from,l_to) VALUES ";
		$now = wfTimestampNow();
		$sql2 = "UPDATE cur SET cur_touched='{$now}' WHERE cur_id IN (";
		$first = true;
		while ( $row = wfFetchObject( $res ) ) {
			if ( ! $first ) { $sql .= ","; $sql2 .= ","; }
			$first = false;

			$sql .= "({$row->bl_from},{$this->mId})";
			$sql2 .= $row->bl_from;
		}
		$sql2 .= ")";
		wfQuery( $sql, DB_WRITE, $fname );
		wfQuery( $sql2, DB_WRITE, $fname );

		$sql = "DELETE FROM brokenlinks WHERE bl_to='{$this->mTitleEnc}'";
		wfQuery( $sql, DB_WRITE, $fname );
	}
	
}

?>