var header = document.querySelector('.enable-sticky');
var cmsEnabled;

if (header) {

  //need to reconfigure sticky header if CMS is active
  if (document.getElementById('cms-header')) {
    cmsEnabled = true;
  }
  else {
    cmsEnabled = false;
  }

  //minWidth - this value is a width value of the viewport. Anything under this viewport under this width wont have sticky. Set to 0 if you want sticky always on.
  var minWidth = 0;

  //stickyStart - this value is the height at which sticky menu can start. When you are scrolled to the top it is 0 so once you scroll to stickyStart value it enables sticky header.
  var stickyStart = 0;

  //jobsEnabled - ff you want sticky header on the job results pages set to true otherwise set to false
  var jobsEnabled = true;

  //stickySearch - if true sticky header will also be applied to job search bar
  var stickySearch = true;
  var delayedStickySearch = 0;

  if (stickySearch) {
    var headerPos = header.getBoundingClientRect();

    var search = document.querySelector('.job-search');
    var searchBrick = document.querySelector('.search-home');
    var homeSearch = document.querySelector('.home-search');
    
    if (searchBrick) {
      var searchBrickPos = searchBrick.getBoundingClientRect();
      var searchBrickHeight = searchBrickPos.height;

      var trigger = delayedStickySearch + searchBrickPos.height - headerPos.height;
    }
  }

  window.addEventListener('scroll', function(e) {
    toggleSticky(minWidth, jobsEnabled);
  });
  window.addEventListener('resize', function(e) {
    toggleSticky(minWidth, jobsEnabled);
  });


  function toggleSticky(minWidth, jobsEnabled) {
    //viewport width
    var vw = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);

    if (jobsEnabled === true) {
      jobsEnabled = -1;
    } else  {
      jobsEnabled = window.location.href.indexOf("jobs?");
    }

    if ((jobsEnabled < 0) && (vw > minWidth)) {
      var lastPosition = window.pageYOffset;

      if ((!header.classList.contains('sticky') && (lastPosition > stickyStart)) || (lastPosition <= stickyStart && header.classList.contains('sticky'))) {
        header.classList.toggle('sticky');
      }

      if (cmsEnabled && searchBrick) {
        searchBrick.classList.add('cmsEnabled');
        document.getElementById('cms-header').style.marginTop = '75px';
      }

      if (searchBrick) {
        if (lastPosition >= trigger && !search.classList.contains('sticky')) {
          search.classList.add('sticky');
          homeSearch.classList.add('sticky');
        }
        if (lastPosition < trigger && search.classList.contains('sticky')) {
          search.classList.remove('sticky');
          homeSearch.classList.remove('sticky');
        }
        if (!header.classList.contains('sticky')) {
          header.classList.add('sticky');
          searchBrick.classList.add('sticky');
        }
      }

    } else if (header.classList.contains('sticky')) {
      //this else if is for window resize when vw is not = 0
      header.classList.remove('sticky');
    }
  }
}