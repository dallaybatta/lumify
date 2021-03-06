var utils = require('../utils');

describe.only('Search', function () {

    before(utils.login);

    it('Should be able to toggle search with menubar', function () {
        return this.browser
          .waitForElementByCss('.menubar-pane .search a').should.eventually.be.ok
          .click()
          .waitFor(this.asserters.jsCondition(utils.animations.openSearchAnimationFinished) , 2000).should.eventually.be.ok
          .waitForElementByCss('.search-query:focus') 
          .elementByCss('.menubar-pane .search a')
          .click()
          .waitFor(this.asserters.jsCondition(utils.animations.closeSearchAnimationFinished) , 2000)
    })

    it('Should be able to toggle search with shortcut', function () {
        return this.browser
          .waitForElementByCss('.menubar-pane .search a').should.eventually.be.ok
          .elementByTagName('body')
          .sendKeys('/')
          .waitFor(this.asserters.jsCondition(utils.animations.openSearchAnimationFinished) , 2000).should.eventually.be.ok
          .waitForElementByCss('.search-query:focus') 
          .elementByTagName('body')
          .sendKeys(this.KEYS.Escape)
          .sendKeys(this.KEYS.Escape)
          .waitFor(this.asserters.jsCondition(utils.animations.closeSearchAnimationFinished) , 2000)
    })

    it('Should be able to search *', function() {
        var browser = this.browser, Q = this.Q;

        return browser
          .waitForElementByCss('.menubar-pane .search a').should.eventually.be.ok
          .click()
          .waitFor(this.asserters.jsCondition(utils.animations.openSearchAnimationFinished) , 2000).should.eventually.exist
          .waitForElementByCss('.search-query:focus').should.eventually.exist
          .type('*')
          .keys(this.KEYS.Return)
          .elementByCss('.search-results').getComputedCss('display').should.become('none')
          .then(function() {
              var concept = 'Raw',
                  expected = '8',
                  el = browser.waitForElementByCss('.search-results-summary a[title=' + concept + '] .badge:not(:empty)');

              return Q.all([
                  el.text().should.become(expected),
                  el.getAttribute('title').should.become(expected),
                  el.click()
              ])
          })
          .elementByCss('.search-results').getComputedCss('display').should.become('block')
          .waitFor(this.asserters.jsCondition("$('.search-results .vertex-item').length === 8") , 500).should.eventually.be.ok
    })

    it('Should be able to drag result to graph', function() {
        return this.browser
            .elementByCss('.vertex-item')
            .moveTo()
            .buttonDown()
            .elementByCss('.graph-pane')
            .moveTo(550, 100)
            .sleep(100)
            .moveTo(600, 150)
            .sleep(100)
            .buttonUp()
            .waitFor(this.asserters.jsCondition("$('.cytoscape-container').cytoscape('get').nodes().length === 1"), 1000)
            .should.eventually.be.ok
            .elementByCss('.graph-pane .controls button[data-event=fit]')
            .click()
            .elementByCss('.graph-pane')
            .moveTo(625,275)
            .buttonDown()
            .buttonUp()
            .sleep(1000)
            .elementByTagName('body')
            .sendKeys(this.KEYS.Delete)
            .sendKeys(this.KEYS['Back space'])
            .waitFor(this.asserters.jsCondition("$('.cytoscape-container').cytoscape('get').nodes().length === 0"), 1000)
            .should.eventually.be.ok
            .waitForElementByCss('.menubar-pane .activity.animating', 1000).should.eventually.exist
            .waitForElementByCss('.menubar-pane .activity:not(.animating)', 10000).should.eventually.exist
    })

})


